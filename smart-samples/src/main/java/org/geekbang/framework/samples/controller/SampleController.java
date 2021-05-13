package org.geekbang.framework.samples.controller;

import org.geekbang.framework.annotation.Action;
import org.geekbang.framework.annotation.Controller;
import org.geekbang.framework.annotation.Inject;
import org.geekbang.framework.bean.Data;
import org.geekbang.framework.samples.service.SampleService;

@Controller
public class SampleController {

    @Inject
    private SampleService sampleService;

    @Action(value = "get:/hello")
    public Data get(){
        return new Data(new Object());
    }
}
