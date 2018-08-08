package com.ram.server.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ram.kettle.util.Const;

public class XssAndSqlHttpServletRequestWrapper extends HttpServletRequestWrapper {  
    
    HttpServletRequest orgRequest = null;  
      
    public XssAndSqlHttpServletRequestWrapper(HttpServletRequest request) {  
        super(request);  
        orgRequest = request;  
    }  
  
    /** 
     * 覆盖getParameter方法，将参数名和参数值都做xss & sql过滤。<br/> 
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/> 
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖 
     */  
    @Override  
    public String getParameter(String name) {   
        String value = super.getParameter(Const.xssEncode(name));  
        if (value != null) {  
            value = Const.xssEncode(value);  
        }  
        return value;  
    }  
  
    /** 
     * 覆盖getHeader方法，将参数名和参数值都做xss & sql过滤。<br/> 
     * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/> 
     * getHeaderNames 也可能需要覆盖 
     */  
    @Override  
    public String getHeader(String name) {  
  
        String value = super.getHeader(Const.xssEncode(name));  
        if (value != null) {  
            value = Const.xssEncode(value);  
        }  
        return value;  
    }  
  

    /** 
     * 获取最原始的request 
     *  
     * @return 
     */  
    public HttpServletRequest getOrgRequest() {  
        return orgRequest;  
    }  
  
    /** 
     * 获取最原始的request的静态方法 
     *  
     * @return 
     */  
    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {  
        if (req instanceof XssAndSqlHttpServletRequestWrapper) {  
            return ((XssAndSqlHttpServletRequestWrapper) req).getOrgRequest();  
        }  
  
        return req;  
    }  
}  
