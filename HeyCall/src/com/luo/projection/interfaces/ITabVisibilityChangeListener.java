package com.zigsun.luo.projection.interfaces;

/**
 * Created by Luo on 2015/5/13.
 * 导航tab
 */
public interface ITabVisibilityChangeListener {

	/**
	 * 切换tab 显示状态
	 */
    void switchTabVisibility();

    /**
     * 隐藏tab
     */
    void hideTab();
    void showTab();

    /**
     * tab 是否可见
     * @return true 可见
     */
    boolean isTabVisible();

}
