package com.ram.beetl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.ext.web.WebRenderExt;

import com.ram.kettle.util.Const;
import com.ram.server.filter.EmContentListener;

public class GlobalExt implements WebRenderExt {
    @Override
    public void modify(Template template, GroupTemplate arg1, HttpServletRequest arg2, HttpServletResponse arg3) {

        if (!Const.isEmpty(EmContentListener.ERR)) {
            String[] errs = EmContentListener.ERR.split(",");
            String[] errInts = EmContentListener.ERRINT.split(",");

            for (int errIndex = 0; errIndex < errs.length; errIndex++) { 
                template.binding( errInts[errIndex], errs[errIndex]);
            }
        }

    }

}
