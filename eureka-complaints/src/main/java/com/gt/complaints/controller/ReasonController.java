package com.gt.complaints.controller;

import com.gt.complaints.entity.Reason;
import com.gt.complaints.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author luke
 */
@RestController
@RequestMapping("/reason")
public class ReasonController {

    @Autowired
    private ReasonService reasonService;

    @PreAuthorize("hasAuthority('complaints:reason:query')")
    @GetMapping("getAll")
    public ResponseEntity getReason(){
        List<Reason> list = reasonService.findAll();
        return new ResponseEntity(list , HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('complaints:reason:save')")
    @PostMapping("save")
    public ResponseEntity save(Reason reason){
        reasonService.save(reason);
        return new ResponseEntity("保存成功" , HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('complaints:reason:update')")
    @PutMapping("update")
    public ResponseEntity update(Reason reason){
        reasonService.update(reason);
        return new ResponseEntity("更新成功" , HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('complaints:reason:delete')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable long id){
        reasonService.deleteReason(id);
        return new ResponseEntity("修改成功" , HttpStatus.OK);
    }
}
