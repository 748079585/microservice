package com.could.quartz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.could.quartz.service.QuartzService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

	@Autowired
	private QuartzService quartzService;

	private static String pre = "com.could.quartz.job.";
	private static String group = "test";

	@PostMapping("/add")
	public void addJob(String name, String cron) {
		log.info("add job : {}", name);
		HashMap<String, String> map = new HashMap<>();
		map.put("name", "sdf");
		quartzService.deleteJob(name, group);
		cron = cron != null ? cron : "0/10 * * * * ?";
		quartzService.addCronJob(getJobBeanByName(name), name, group, cron, map);
	}

	@PostMapping("/delete")
	public void deleteJob(String name) {
		log.info("delete job :{}", name);
		quartzService.deleteJob(name, group);
	}

	@PostMapping("/start")
	public void startJob(String name) {
		log.info("run job :{} right now", name);
		quartzService.runAJobNow(name, group);
	}

	@PostMapping("/update/cron")
	public void updateJob(String name, String cron) {
		log.info("update job:{} , cron: {}", name, cron);
		quartzService.updateJob(name, group, cron);
	}

	@PostMapping("/query/alljob")
	public List<Map<String, Object>> queryAllJob() {
		return quartzService.queryAllJob();
	}

	@PostMapping("/query/runjob")
	public List<Map<String, Object>> queryRunJob() {
		return quartzService.queryRunJob();
	}

	@SuppressWarnings("unchecked")
	private Class<? extends QuartzJobBean> getJobBeanByName(String name) {
		try {
			return (Class<? extends QuartzJobBean>) Class.forName(pre + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
