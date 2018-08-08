package com.ram.server.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {
	/**
	 * Get cookie value by cookie name.
	 */
	public static String getCookie(HttpServletRequest request, String name,
			String defaultValue) {
		Cookie cookie = getCookieObject(request, name);
		return cookie != null ? cookie.getValue() : defaultValue;
	}

	public static Cookie getCookieObject(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name) && cookie.getValue() != null
						&& !cookie.getValue().equals(""))
					return cookie;
		return null;
	}
}
