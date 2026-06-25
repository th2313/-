package com.example.keshe.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.keshe.entity.Patientinfo;
import java.util.List;

/**
 * 病人信息服务接口
 */
public interface IPatientinfoService extends IService<Patientinfo> {
    boolean savePatient(Patientinfo patientinfo);
    Integer countByToday();

    // 按姓名模糊查询
    List<Patientinfo> findByNameLike(String name);
}