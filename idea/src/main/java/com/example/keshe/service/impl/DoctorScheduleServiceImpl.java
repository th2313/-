package com.example.keshe.service.impl;

import com.example.keshe.entity.DoctorSchedule;
import com.example.keshe.mapper.DoctorScheduleMapper;
import com.example.keshe.service.IDoctorScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DoctorScheduleServiceImpl extends ServiceImpl<DoctorScheduleMapper, DoctorSchedule> implements IDoctorScheduleService {
}