package com.ly.pet.service.impl;

import com.ly.pet.service.IAnimalService;
import com.ly.pet.entity.Animal;
import com.ly.pet.mapper.AnimalMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-04-02
 */
@Service
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements IAnimalService {

}
