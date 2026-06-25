package com.example.keshe.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.keshe.entity.Fmeditem;
import com.example.keshe.service.IFmeditemService;
import com.example.keshe.service.IPatientinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查项目控制器
 */
@RestController
@RequestMapping("/api/fmeditem")
@CrossOrigin(origins = "*") // 允许跨域访问，生产环境建议指定具体前端域名
public class FmeditemController {

    @Autowired
    private IFmeditemService fmeditemService;

    /**
     * 分页查询检查项目
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseEntity<IPage<Fmeditem>> getPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Fmeditem> page = new Page<>(pageNum, pageSize);
        IPage<Fmeditem> result = fmeditemService.page(page);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询所有检查项目
     * @return 检查项目列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Fmeditem>> getAll() {
        return ResponseEntity.ok(fmeditemService.list());
    }

    /**
     * 根据ID查询检查项目
     * @param id 检查项目ID
     * @return 检查项目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Fmeditem> getById(@PathVariable Integer id) {
        Fmeditem fmeditem = fmeditemService.getById(id);
        if (fmeditem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fmeditem);
    }

    /**
     * 添加检查项目
     * @param fmeditem 检查项目信息
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<Boolean> add(@RequestBody Fmeditem fmeditem) {
        // 设置创建时间和最后修改时间
        fmeditem.setCreationDate(java.time.LocalDateTime.now());
        fmeditem.setLastUpdateDate(java.time.LocalDateTime.now());
        // 默认删除标记为0（未删除）
        if (fmeditem.getDelMark() == null) {
            fmeditem.setDelMark(0);
        }
        boolean success = fmeditemService.save(fmeditem);
        return new ResponseEntity<>(success, HttpStatus.CREATED);
    }

    /**
     * 更新检查项目
     * @param id 检查项目ID
     * @param fmeditem 检查项目信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> update(@PathVariable Integer id, @RequestBody Fmeditem fmeditem) {
        // 验证ID是否匹配
        if (!id.equals(fmeditem.getId())) {
            return ResponseEntity.badRequest().body(false);
        }

        // 检查记录是否存在
        if (!fmeditemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 更新最后修改时间
        fmeditem.setLastUpdateDate(java.time.LocalDateTime.now());
        boolean success = fmeditemService.updateById(fmeditem);
        return ResponseEntity.ok(success);
    }

    /**
     * 删除检查项目（逻辑删除）
     * @param id 检查项目ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        if (!fmeditemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // 逻辑删除：更新删除标记为1
        Fmeditem fmeditem = new Fmeditem();
        fmeditem.setId(id);
        fmeditem.setDelMark(1);
        fmeditem.setLastUpdateDate(java.time.LocalDateTime.now());

        boolean success = fmeditemService.updateById(fmeditem);
        return ResponseEntity.ok(success);
    }

    /**
     * 根据科室ID查询检查项目
     * @param deptId 科室ID
     * @return 检查项目列表
     */
    @GetMapping("/byDept/{deptId}")
    public ResponseEntity<List<Fmeditem>> getByDeptId(@PathVariable Integer deptId) {
        List<Fmeditem> list = fmeditemService.getByDeptId(deptId);
        return ResponseEntity.ok(list);
    }

}
