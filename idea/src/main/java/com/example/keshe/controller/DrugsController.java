package com.example.keshe.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.keshe.entity.Drugs;
import com.example.keshe.service.IDrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drugs")
public class DrugsController {

    @Autowired
    private IDrugsService drugsService;

    /**
     * 新增药品
     */
    @PostMapping
    public boolean save(@RequestBody Drugs drugs) {
        return drugsService.save(drugs);
    }

    /**
     * 修改药品信息
     */
    @PutMapping
    public boolean update(@RequestBody Drugs drugs) {
        return drugsService.updateById(drugs);
    }

    /**
     * 根据ID删除药品（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        Drugs drugs = new Drugs();
        drugs.setId(id);
        drugs.setDelMark(1); // 逻辑删除标记
        return drugsService.updateById(drugs);
    }

    /**
     * 根据ID查询药品
     */
    @GetMapping("/{id}")
    public Drugs getById(@PathVariable Integer id) {
        return drugsService.getById(id);
    }

    /**
     * 查询所有药品列表
     */
    @GetMapping
    public List<Drugs> listAll() {
        QueryWrapper<Drugs> wrapper = new QueryWrapper<>();
        wrapper.eq("del_mark", 0); // 只查询未删除的
        return drugsService.list(wrapper);
    }

    /**
     * 分页查询药品
     */
    @GetMapping("/page")
    public Page<Drugs> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String drugsName,
            @RequestParam(required = false) String drugsCode) {

        Page<Drugs> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Drugs> wrapper = new QueryWrapper<>();
        wrapper.eq("del_mark", 0);

        if (drugsName != null && !drugsName.isEmpty()) {
            wrapper.like("drugs_name", drugsName);
        }
        if (drugsCode != null && !drugsCode.isEmpty()) {
            wrapper.eq("drugs_code", drugsCode);
        }

        return drugsService.page(page, wrapper);
    }

    /**
     * 根据药品名称模糊查询
     */
    @GetMapping("/search")
    public List<Drugs> searchByName(@RequestParam String drugsName) {
        QueryWrapper<Drugs> wrapper = new QueryWrapper<>();
        wrapper.like("drugs_name", drugsName)
                .eq("del_mark", 0);
        return drugsService.list(wrapper);
    }
}