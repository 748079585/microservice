package com.gt.complaints.controller;

import com.gt.complaints.entity.Complaint;
import com.gt.complaints.service.ComplaintsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author luke
 */
@RestController
@RequestMapping("/complaint")
public class ComplaintsController {

    @Autowired
    private ComplaintsService complaintsService;

    /**
     * 图片上传存储的本地根路径
     */
    @Value("${complaint.picture.path}")
    private String uploadPath;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PreAuthorize("hasAuthority('complaints:complaint:query')")
    @GetMapping("findAll")
    public List<Complaint> findAll(){
       return complaintsService.findAll();
    }


    /**
     * 通过ID查询投诉信息
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyAuthority('complaints:complaint:findById','complaints:complaint:query')")
    @GetMapping("find/{id}")
    public ResponseEntity findById(@PathVariable long id){
         List<Complaint> list = complaintsService.findByUserId(id);
         return new ResponseEntity(list , HttpStatus.OK);
    }


    @ApiOperation(value = "投诉接口",notes = "投诉接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "files",value = "多个文件",paramType = "formData",allowMultiple=true,required = true,dataType = "file"),
    })
    @PreAuthorize("hasAuthority('complaints:complaint:save')")
    @PostMapping(value = "/add", headers = "content-type=multipart/form-data")
    public ResponseEntity save(@RequestParam(value = "files")MultipartFile[] files , Complaint complaint){
        List<String> picPaths = new ArrayList<>();
        // 图片保存到本地
        if (files != null && files.length >= 1) {
            try {
                for (MultipartFile file : files) {
                    String fileName = file.getOriginalFilename();
                    //判断是否有文件且是否为图片文件
                    if (fileName != null && !"".equalsIgnoreCase(fileName.trim()) && isImageFile(fileName)) {
                        // 按天为单位建立图片路径
                        File dir = new File(uploadPath + File.separator + LocalDateTime.now().format(formatter));
                        if(!dir.exists()){
                            dir.mkdir();
                        }
                        //创建输出文件对象
                        File outFile = new File(dir + File.separator + UUID.randomUUID()  + ".png");
                        //拷贝文件到输出文件对象
                        FileUtils.copyInputStreamToFile(file.getInputStream(), outFile);
                        picPaths.add(outFile.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("上传图片错误！");
            }
        }
        // 保存数据到数据库
        complaintsService.save(complaint , picPaths);
        return new ResponseEntity("保存成功",HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('complaints:complaint:delete')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id){
        complaintsService.delete(id);
        return new ResponseEntity("删除成功",HttpStatus.OK);
    }

    /**
     * 判断文件是否为图片格式
     * @param fileName
     * @return
     */
    private Boolean isImageFile(String fileName) {
        String[] img_type = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        if (fileName == null) {
            return false;
        }
        fileName = fileName.toLowerCase();
        for (String type : img_type) {
            if (fileName.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

}
