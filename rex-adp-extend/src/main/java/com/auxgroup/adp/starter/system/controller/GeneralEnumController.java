package com.auxgroup.adp.starter.system.controller;

import com.auxdemo.adp.commons.group.config.enums.EnumStaticMap;
import com.auxgroup.adp.commons.group.beans.AuxResponse;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/general/enums")
public class GeneralEnumController {
    @PostMapping(value = "/getEnumTypes")
    public AuxResponse getEnumTypes(@RequestBody List<String> types){
        if(CollectionUtils.isEmpty(types)){
            return null;
        }
        return AuxResponse.SUCCESS().data(EnumStaticMap.getByTypeList(types));
    }
    @GetMapping(value = "/getAllEnumTypes")
    public AuxResponse getAllEnumTypes(){
        return AuxResponse.SUCCESS().data(EnumStaticMap.getByTypeList(Lists.newArrayList("$$$ALL$$$")));
    }
}
