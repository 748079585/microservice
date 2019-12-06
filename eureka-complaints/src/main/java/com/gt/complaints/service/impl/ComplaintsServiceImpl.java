package com.gt.complaints.service.impl;

import com.gt.complaints.dao.ComplaintsDao;
import com.gt.complaints.dao.PictureDao;
import com.gt.complaints.entity.Complaint;
import com.gt.complaints.entity.Picture;
import com.gt.complaints.service.ComplaintsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.Transient;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class ComplaintsServiceImpl implements ComplaintsService {

    @Autowired
    private ComplaintsDao complaintsDao;

    @Autowired
    private PictureDao pictureDao;

    @Value("${server.port}")
    private int port;

    /**
     * 图片上传存储的本地根路径
     */
    @Value("${complaint.picture.path}")
    private String uploadPath;

    @Override
    public void save(Complaint complaints, List<String> picPaths) {
        List<Picture> pictureList = new ArrayList<>();
        for (String path: picPaths) {
            Picture picture = new Picture();
            // 截取图片映射路径后的存储地址
            String name = path.substring(uploadPath.length());
            // 获取IP地址
            String ip = this.getLocalIpAddress();
            // 图片映射路径
            String url = "http://" + ip + ":" + port + "/picture" + name;
            picture.setUrl(url);
            picture.setPath(path);
            pictureDao.save(picture);
            pictureList.add(picture);
        }
        complaints.setPictures(pictureList);
        complaintsDao.save(complaints);
    }

    @Override
    public List<Complaint> findAll() {
        return complaintsDao.findAll();
    }

    @Override
    public List<Complaint> findByUserId(long userId) {
        return complaintsDao.findByUserId(userId);
    }

    @Override
    @Transient
    public void delete(long id) {
        complaintsDao.deleteById(id);
    }

    /**
     * 获取本机IP
     *
     * @return
     * @throws SocketException
     */
    public String getLocalIpAddress() {
        // 本地IP，如果没有配置外网IP则返回它
        String localIp = null;
        // 外网IP
        String netIp = null;

        try{
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            // 外网IP
            boolean isFound = false;
            while (netInterfaces.hasMoreElements() && !isFound) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        // 外网IP
                        netIp = ip.getHostAddress();
                        isFound = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        // 外网IP
                        localIp = ip.getHostAddress();
                    }
                }
            }

            if (netIp != null && !"".equals(netIp)) {
                return netIp;
            } else {
                return localIp;
            }
        }catch (Exception e){
            return null;
        }
    }
}
