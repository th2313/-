package com.example.keshe.controller;

import com.example.keshe.dto.Result;
import com.example.keshe.entity.VisitRecord;
import com.example.keshe.service.IVisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 多次就医记录控制器
 */
@RestController
@RequestMapping("/visitRecord")
public class VisitRecordController {

    @Autowired
    private IVisitRecordService visitRecordService;

    /**
     * 根据患者hspID查询所有就医记录
     */
    @GetMapping("/hsp/{hspID}")
    public Result<List<VisitRecord>> getByHspID(@PathVariable String hspID) {
        List<VisitRecord> records = visitRecordService.findByHspID(hspID);
        return Result.success(records);
    }

    /**
     * 查询所有就医记录
     */
    @GetMapping
    public Result<List<VisitRecord>> listAll() {
        List<VisitRecord> records = visitRecordService.list();
        return Result.success(records);
    }

    /**
     * 根据ID查询就医记录
     */
    @GetMapping("/{id}")
    public Result<VisitRecord> getById(@PathVariable Long id) {
        VisitRecord record = visitRecordService.getById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error(404, "未找到该就医记录");
    }

    /**
     * 新增就医记录
     */
    @PostMapping
    public Result<String> save(@RequestBody VisitRecord visitRecord) {
        boolean success = visitRecordService.save(visitRecord);
        if (success) {
            return Result.success("新增成功");
        }
        return Result.error(500, "新增失败");
    }

    /**
     * 更新就医记录
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody VisitRecord visitRecord) {
        visitRecord.setId(id);
        boolean success = visitRecordService.updateById(visitRecord);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error(404, "未找到该就医记录，更新失败");
    }

    /**
     * 删除就医记录
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = visitRecordService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error(404, "未找到该就医记录，删除失败");
    }
}
