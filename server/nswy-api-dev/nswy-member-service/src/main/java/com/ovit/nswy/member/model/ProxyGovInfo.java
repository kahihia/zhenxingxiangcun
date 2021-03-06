package com.ovit.nswy.member.model;

import java.util.Date;

/**
 * 机关乡村代理表
 */
public class ProxyGovInfo {
    private Integer id;

    private String govName;

    private String loginAccount;

    private String organizationCode;

    private Date createTime;

    private String govType;

    private String govLevel;

    private String location;

    private int approveStatus;

    private String address;

    private String countryType;

    private String unitPersonPictureList;

    private String infoId;

    private String qualificationCertificatePictureList;

    private String addrDetail;

    public String getAddrDetail() {
        return addrDetail;
    }

    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getGovType() {
        return govType;
    }

    public void setGovType(String govType) {
        this.govType = govType;
    }

    public String getGovLevel() {
        return govLevel;
    }

    public void setGovLevel(String govLevel) {
        this.govLevel = govLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGovName() {
        return govName;
    }

    public void setGovName(String govName) {
        this.govName = govName == null ? null : govName.trim();
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount == null ? null : loginAccount.trim();
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode == null ? null : organizationCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCountryType() {
        return countryType;
    }

    public void setCountryType(String countryType) {
        this.countryType = countryType;
    }

    public String getUnitPersonPictureList() {
        return unitPersonPictureList;
    }

    public void setUnitPersonPictureList(String unitPersonPictureList) {
        this.unitPersonPictureList = unitPersonPictureList;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getQualificationCertificatePictureList() {
        return qualificationCertificatePictureList;
    }

    public void setQualificationCertificatePictureList(String qualificationCertificatePictureList) {
        this.qualificationCertificatePictureList = qualificationCertificatePictureList;
    }
}