package com.ovit.nswy.member.web.product.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ovit.nswy.member.common.enums.CertificationStepEnum;
import com.ovit.nswy.member.model.LoginUser;
import com.ovit.nswy.member.service.AppSettingsService;
import com.ovit.nswy.member.service.LoginUserService;
import com.ovit.nswy.member.service.WebsiteSettingsService;
import com.ovit.nswy.member.web.base.Result;
import com.ovit.nswy.member.web.base.ResultCode;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/websiteSettings")
public class WebsiteSettingsController {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private WebsiteSettingsService websiteSettingsService;

    @Autowired
    private LoginUserService loginUserService;


    /**
     * 保存网站设置信息，存在则更新
     * @param params
     * @return
     */
    @RequestMapping(value = "/saveOrUpdateWebsiteSettingsInfo", method = RequestMethod.POST)
    public Result saveOrUpdateWebsiteSettingsInfo(@RequestBody Map<String, Object> params) {
        Result result = new Result();
        try {
            Map<String,Object> websiteInfo = (Map<String, Object>) params.get("websiteInfo");
            //String websiteTemplateInfo = JSON.toJSONString(params.get("templateData"));
            //String websiteModuleInfo = JSON.toJSONString(params.get("moduleData"));
            //logger.info("网站模板信息转为json：{}, 模块信息转为json：{}", websiteTemplateInfo, websiteModuleInfo);
            String account = MapUtils.getString(params,"account");
            String userType = MapUtils.getString(params,"userType");

            Map<String,Object> websiteSettingsMap = new HashMap<>();
            websiteSettingsMap.put("account",account);
            //websiteSettingsMap.put("template_info",websiteTemplateInfo);
            //websiteSettingsMap.put("module_info",websiteModuleInfo);
            websiteSettingsMap.put("website_name", websiteInfo.get("websiteName"));
            websiteSettingsMap.put("is_show", websiteInfo.get("isShowWebsiteName"));
            websiteSettingsMap.put("website_logo", websiteInfo.get("websiteLOGO"));
            websiteSettingsMap.put("website_banner", websiteInfo.get("websiteBanner"));
            websiteSettingsMap.put("website_profile", websiteInfo.get("websiteProfile"));
            websiteSettingsMap.put("name_suffix", websiteInfo.get("nameSuffix"));
            //websiteSettingsMap.put("website_introduction", websiteInfo.get("websiteIntroduction"));
            websiteSettingsMap.put("userType", userType);

            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("account", account);
            accountInfo.put("userType", userType);
            Map<String,Object> historyMap = websiteSettingsService.findWebsiteSettingsByAccount(accountInfo);
            if (MapUtils.isEmpty(historyMap)) {
                //无历史数据，则新增数据
                websiteSettingsService.saveWebsiteSettings(websiteSettingsMap);

            } else {
                //有历史数据，则更新数据
                websiteSettingsService.updateWebsiteSettings(websiteSettingsMap);
            }

            //保存认证步骤到登陆表中
            loginUserService.updateIdentityFlag(account, String.valueOf(CertificationStepEnum.STEP_COLUMNSETTINGS.getStepNum()));
            logger.info("网站设置信息保存成功");

        } catch (Exception e) {
            logger.error("网站设置信息保存失败: {}",e);
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMsg("保存网站设置信息失败");
        }
        return result;
    }

    /**
     * 查询网站设置信息
     */
    @RequestMapping(value = "/findWebsiteSettingsInfo", method = RequestMethod.POST)
    public Result findWebsiteSettingsInfo(@RequestBody Map<String,Object> params) {
        Result result = new Result();
        try {
            String account = MapUtils.getString(params,"account");
            //String userType = MapUtils.getString(params,"userType");
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("account", account);
            //accountInfo.put("userType", userType);
            Map<String,Object> websiteSettingsInfo = websiteSettingsService.findWebsiteSettingsByAccount(accountInfo);
            if (MapUtils.isNotEmpty(websiteSettingsInfo)) {
                //已认证过网站设置步骤，存在数据
                Map<String,Object> websiteInfo = new HashMap<>();
                websiteInfo.put("websiteName", websiteSettingsInfo.get("website_name"));
                websiteInfo.put("isShowWebsiteName", "1".equals(MapUtils.getString(websiteSettingsInfo,"is_show")) ? true : false);
                websiteInfo.put("websiteLOGO", websiteSettingsInfo.get("website_logo"));
                websiteInfo.put("websiteBanner", websiteSettingsInfo.get("website_banner"));
                websiteInfo.put("websiteProfile", websiteSettingsInfo.get("website_profile"));

                websiteInfo.put("nameSuffix", websiteSettingsInfo.get("name_suffix"));
                //websiteInfo.put("websiteIntroduction", websiteSettingsInfo.get("website_introduction"));
                //JSONArray templateMap = JSONArray.parseArray(websiteSettingsInfo.get("template_info").toString());
                //JSONArray moduleMap = JSONArray.parseArray(websiteSettingsInfo.get("module_info").toString());
                Map<String,Object> wensiteDetailInfo = new HashMap<>();
                /*wensiteDetailInfo.put("templateData", templateMap);
                wensiteDetailInfo.put("moduleData", moduleMap);*/
                wensiteDetailInfo.put("websiteInfo", websiteInfo);

                result.setData(wensiteDetailInfo);
            } else {
                //无数据，初始化网站设置，返回对应类型的所有模板、模块
                //Map<String,Object> initializeWebsiteInfo =  websiteSettingsService.findAllWebsiteInfo(userType);

                Map<String,Object> initializeWebsiteInfo = new HashMap<>();
                //若为专家，无数据时，需要将其个人的数据中的websiteInfo传到前端
                Map<String, Object> historyInfo = new HashMap<>();
                historyInfo.put("account", account);
                Map<String,Object> historyMap = websiteSettingsService.findWebsiteSettingsByAccount(historyInfo);
                if (MapUtils.isNotEmpty(historyMap)) {
                    Map<String,Object> websiteInfo = new HashMap<>();
                    websiteInfo.put("websiteName", historyMap.get("website_name"));
                    websiteInfo.put("isShowWebsiteName",  "1".equals(MapUtils.getString(historyMap,"is_show")) ? true : false);
                    websiteInfo.put("websiteLOGO", historyMap.get("website_logo"));
                    websiteInfo.put("websiteBanner", historyMap.get("website_banner"));
                    websiteInfo.put("websiteProfile", historyMap.get("website_profile"));
                    websiteInfo.put("nameSuffix", historyMap.get("name_suffix"));
                    //websiteInfo.put("websiteIntroduction", historyMap.get("website_introduction"));
                    initializeWebsiteInfo.put("websiteInfo", websiteInfo);
                }
                result.setData(initializeWebsiteInfo);
            }

        } catch (Exception e) {
            logger.error(e);
            result.setCode(ResultCode.INTERNAL_SERVER_ERROR);
            result.setMsg("查询网站设置信息失败");
        }
        logger.info("查询网站设置信息为： {}", result.getData());
        return result;
    }
}
