package com.faceghost.elasticbg.controller;

import com.faceghost.elasticbg.base.controller.BaseController;
import com.faceghost.elasticbg.base.model.SystemUser;
import com.faceghost.elasticbg.base.service.SystemLogService;
import com.faceghost.elasticbg.base.service.SystemUserService;
import com.faceghost.elasticbg.base.statics.ErrorMsgConst;
import com.faceghost.elasticbg.base.statics.LogType;
import com.faceghost.elasticbg.base.utils.ExceptionUtil;
import com.faceghost.elasticbg.base.utils.IPUtil;
import com.faceghost.elasticbg.base.utils.PasswordUtil;
import com.faceghost.elasticbg.base.utils.ValidateUtil;
import com.faceghost.elasticbg.base.vo.BaseVo;
import com.faceghost.elasticbg.base.vo.ExtjsCheckTreeVo;
import com.faceghost.elasticbg.base.vo.PageVo;
import com.faceghost.elasticbg.base.vo.SystemUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户管理
 */
@Slf4j
@Controller
@RequestMapping("/systemUser")
public class SystemUserController  extends BaseController{

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private PasswordUtil passwordUtil;

	@Autowired
	private SystemLogService systemLogService;

	private static final String clazzName = SystemUserController.class.getSimpleName();
	/**
	 * 用户管理-分页显示
	 * @param searchVo
	 * @return
	 */
	@RequiresPermissions("system:user:view")
	@RequestMapping(value="getSystemUserPageVo")
	@ResponseBody
	public Object getSystemUserPageVo(SystemUserVo searchVo){
		PageVo pageList = new PageVo();
		try {
			pageList = systemUserService.getSystemUserPageVo(searchVo);
		} catch (Exception e) {
			log.error(String.format("执行：用户管理-分页显示，异常：%s", ExceptionUtil.getExDetail(e)));
		}
		return pageList;
	}
	
	/**
	 * 用户管理-获取角色
	 * @return
	 */
	@RequestMapping(value="getSystemUserRole")
	@ResponseBody
	public Object getSystemUserRole(Integer orgId,String userId){
		List<ExtjsCheckTreeVo> root = new ArrayList<ExtjsCheckTreeVo>();
		try {
			if(orgId == null && userId == null) {
				return root;
			}
			return systemUserService.getSystemUserRole(orgId,userId);
		} catch (Exception e) {
			log.error(String.format("执行：用户管理-获取角色，异常：%s", ExceptionUtil.getExDetail(e)));
		}
		return root;
	}
	
	
	/**
	 * 用户管理-新增
	 * @param bean
	 * @return
	 */
	@RequiresPermissions("system:user:add")
	@RequestMapping(value = "/execAddSystemUser")
	@ResponseBody
	public BaseVo execAddSystemUser(SystemUser bean,String statusStr,String userRoles){
		String methodName = "execAddSystemUser";
		String operName = "用户管理-新增";
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		try {
			if(bean.getSystemOrgId() == null || (ValidateUtil.validateBlank(bean.getId()) && ValidateUtil.validateBlank(bean.getName()))) {
				vo.setMsg(ErrorMsgConst.errParam);
				return vo;
			}

			bean.setId(null);
			bean.setInsertU(getLoginUser().getId());
			bean.setInsertT(new Date());
			bean.setName(bean.getName().trim());
			if("on".equals(statusStr)) {
				bean.setStatus((byte)1);
			}else {
				bean.setStatus((byte)0);
			}
			bean =  systemUserService.execAddSystemUser(bean,userRoles);
			if(!ValidateUtil.validateBlank(bean.getId())) {
				vo.setSuccess(Boolean.TRUE);
				systemLogService.saveLog
						(
								LogType.BUSI.getType(),
								getLoginUser().getId(),
								"执行：" + operName,
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);
			}

		}catch (Exception e) {
			BaseVo eVo = ExceptionUtil.dealRpcError(e);
			vo.setMsg(eVo.getMsg());
			if(eVo.getSuccess()){
				return vo;
			}else{
				log.error(String.format("执行：%s，异常：%s",operName, ExceptionUtil.getExDetail(e)));
				systemLogService.saveLog
						(
								LogType.EX.getType(),
								getLoginUser().getId(),
								ExceptionUtil.getExDetail_log(e),
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);

			}
		}
		return vo;
    }

	/**
	 * 用户管理-更新
	 * @param bean
	 * @return
	 */
	@RequiresPermissions("system:user:update")
	@RequestMapping(value = "/execUpdateSystemUser")
	@ResponseBody
	public BaseVo execUpdateSystemUser(SystemUser bean,String statusStr,String userRoles){
		String methodName = "execUpdateSystemUser";
		String operName = "用户管理-更新";
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		try {
			if(bean.getId() == null
					|| bean.getSystemOrgId() == null
					|| (ValidateUtil.validateBlank(bean.getId()) && ValidateUtil.validateBlank(bean.getName()))) {
				vo.setMsg(ErrorMsgConst.errParam);
				return vo;
			}
			bean.setUpdateT(new Date());
			bean.setUpdateU(getLoginUser().getId());
			if(!ValidateUtil.validateBlank(bean.getName())){
				bean.setName(bean.getName().trim());
			}

			if("on".equals(statusStr)) {
				bean.setStatus((byte)1);
			}else {
				bean.setStatus((byte)0);
			}
			bean =  systemUserService.execAddSystemUser(bean,userRoles);
			vo.setSuccess(Boolean.TRUE);
			vo.setMsg("");
			systemLogService.saveLog
					(
							LogType.BUSI.getType(),
							getLoginUser().getId(),
							"执行：" + operName,
							String.valueOf(bean.getId()),
							clazzName,
							methodName,
							IPUtil.getInnerIpAddress(request),
							IPUtil.getOuterIpAddress(request)
					);

		}catch (Exception e) {
			BaseVo eVo = ExceptionUtil.dealRpcError(e);
			vo.setMsg(eVo.getMsg());
			if(eVo.getSuccess()){
				return vo;
			}else{
				log.error(String.format("执行：%s，异常：%s",operName,ExceptionUtil.getExDetail(e)));
				systemLogService.saveLog
						(
								LogType.EX.getType(),
								getLoginUser().getId(),
								ExceptionUtil.getExDetail_log(e),
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);
			}

		}
		return vo;
	}
	
	
	/**
	 * 用户管理-新修改用户 获取原数据
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/preExecOperSystemUser")
	@ResponseBody
	public BaseVo preExecAddSystemUser(SystemUser bean){
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		try {
			if(ValidateUtil.validateBlank(bean.getId())) {
				vo.setMsg(ErrorMsgConst.errParam);
				return vo;
			}
			SystemUserVo data = systemUserService.preExecOperSystemUser(bean.getId());
			vo.setData(data);
			vo.setSuccess(Boolean.TRUE);
			vo.setMsg("");
		}catch (Exception e) {
			log.error(String.format("执行：用户管理-新修改用户 获取原数据，异常：%s", ExceptionUtil.getExDetail(e)));
		}
		return vo;
    }


	/**
	 * 用户管理-重置密码
	 * @param id
	 * @return
	 */
	@RequiresPermissions("system:user:resetPwd")
	@RequestMapping(value = "/resetSystemUserPwd")
	@ResponseBody
	public BaseVo resetSystemUserPwd(String id){
		String methodName = "resetSystemUserPwd";
		String operName = "用户管理-重置密码";
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		try {
			if(ValidateUtil.validateBlank(id)) {
				vo.setMsg(ErrorMsgConst.errParam);
				return vo;
			}
			systemUserService.resetSystemUserPwd(id);
			vo.setSuccess(Boolean.TRUE);
			vo.setMsg("");
			systemLogService.saveLog
					(
							LogType.BUSI.getType(),
							getLoginUser().getId(),
							"执行：" + operName,
							id,
						 	clazzName,
							methodName,
							IPUtil.getInnerIpAddress(request),
							IPUtil.getOuterIpAddress(request)
					);
		}catch (Exception e) {
			BaseVo eVo = ExceptionUtil.dealRpcError(e);
			vo.setMsg(eVo.getMsg());
			if(eVo.getSuccess()){
				return vo;
			}else{
				log.error(String.format("执行：%s，异常：%s", operName,ExceptionUtil.getExDetail(e)));
				systemLogService.saveLog
						(
								LogType.EX.getType(),
								getLoginUser().getId(),
								ExceptionUtil.getExDetail_log(e),
								id,
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);

			}
		}
		return vo;
	}


	/**
	 * 修改密码
	 * @param oldPwd
	 * @param pwd
	 * @param newPwd
	 * @return
	 */
	@RequestMapping(value = "/changePwd")
	@ResponseBody
	public BaseVo changePwd(String oldPwd,String pwd,String newPwd){
		String methodName = "changePwd";
		String operName = "修改密码";
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		SystemUser bean = null;
		try {
			if(ValidateUtil.validateBlank(oldPwd) || ValidateUtil.validateBlank(pwd) || ValidateUtil.validateBlank(newPwd)){
				return vo;
			}else if(!pwd.trim().equals(newPwd.trim())){
				vo.setMsg("两次密码输入不一致！");
				return vo;
			}else if(oldPwd.trim().equals(pwd.trim())){
				vo.setMsg("新密码不能与旧密码一致！");
				return vo;
			}else{

				bean = systemUserService.getSystemUserByUserName(getLoginUser().getName());
				if(bean == null){
					vo.setMsg("未查询到当前登录的相关信息！");
					return vo;
				}
				String encryptPwd = PasswordUtil.encryptPassword(oldPwd,bean.getId(),bean.getSalt());
				if(!encryptPwd.equals(bean.getPassword())){
					vo.setMsg("原密码输入不正确！");
					return vo;
				}
				bean.setPassword(newPwd);
				passwordUtil.systemUserEncryptPassword(bean);
				int eff = systemUserService.updateBean(bean);
				if(eff > 0){
					vo.setSuccess(Boolean.TRUE);
					vo.setMsg("");
					systemLogService.saveLog
							(
									LogType.BUSI.getType(),
									getLoginUser().getId(),
									"执行：" + operName,
									String.valueOf(bean.getId()),
									clazzName,
									methodName,
									IPUtil.getInnerIpAddress(request),
									IPUtil.getOuterIpAddress(request)
							);
				}
			}

		} catch (Exception e) {
			BaseVo eVo = ExceptionUtil.dealRpcError(e);
			vo.setMsg(eVo.getMsg());
			if(eVo.getSuccess()){
				return vo;
			}else{
				log.error(String.format("执行：%s，异常：%s", operName,ExceptionUtil.getExDetail(e)));
				systemLogService.saveLog
						(
								LogType.EX.getType(),
								getLoginUser().getId(),
								ExceptionUtil.getExDetail_log(e),
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);
			}
		}
		return vo;
	}


	/**
	 * 个人设置-更新
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/execUpdateUserInfo")
	@ResponseBody
	public BaseVo execUpdateUserInfo(SystemUser bean){
		String methodName = "execUpdateUserInfo";
		String operName = "个人设置";
		BaseVo vo = new BaseVo();
		vo.setSuccess(Boolean.FALSE);
		vo.setMsg(ErrorMsgConst.errOperFail);
		try {
			if(ValidateUtil.validateBlank(bean.getId())) {
				vo.setMsg(ErrorMsgConst.errParam);
				return vo;
			}
			bean.setUpdateT(new Date());
			bean.setUpdateU(getLoginUser().getId());
			int eff =  systemUserService.updateBean(bean);
			if(eff > 0) {
				vo.setSuccess(Boolean.TRUE);
				vo.setMsg("");
				systemLogService.saveLog
						(
								LogType.BUSI.getType(),
								getLoginUser().getId(),
								"执行：" + operName,
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);
			}
		}catch (Exception e) {
			BaseVo eVo = ExceptionUtil.dealRpcError(e);
			vo.setMsg(eVo.getMsg());
			if(eVo.getSuccess()){
				return vo;
			}else{
				log.error(String.format("执行：%s，异常：%s", operName,ExceptionUtil.getExDetail(e)));
				systemLogService.saveLog
						(
								LogType.EX.getType(),
								getLoginUser().getId(),
								ExceptionUtil.getExDetail_log(e),
								String.valueOf(bean.getId()),
								clazzName,
								methodName,
								IPUtil.getInnerIpAddress(request),
								IPUtil.getOuterIpAddress(request)
						);
			}
		}
		return vo;
	}

}
