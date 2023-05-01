package com.cimss.project.controller;

import com.cimss.project.apibody.ManageBean;
import com.cimss.project.apibody.MessageBean;
import com.cimss.project.database.entity.Group;
import com.cimss.project.database.entity.User;
import com.cimss.project.service.AuthorizationService;
import com.cimss.project.service.CIMSService;
import com.cimss.project.service.WebhookService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "跨即時通訊軟體微服務API")
@RestController
public class InstantMessageController {
	
	@Autowired
    private CIMSService cimsService;

	@Autowired
	private WebhookService webhookService;

	@Autowired
	private AuthorizationService authorizationService;

	//get method

	@Hidden
	@GetMapping("/")
	public MessageBean home() {
		return MessageBean.CreateMessageBean("Hello!");
	}
	@Operation(summary = "取得用戶資料", description = "必須用該群組的API KEY，否則會驗證失敗(無回傳)")
	@GetMapping("/groups/{groupId}/members")
	public List<User> getMembers(@RequestHeader(name = "Authorization") String accessToken,
								 @Parameter(description = "該群組的ID，由6位英數字組合的字串", example = "AbCd12")
								 @PathVariable String groupId){
		if(!authorizationService.authorization(accessToken,"",groupId).managerPermission())
			return null;
		return cimsService.getMembers(groupId);
	}

	//post method
	@Operation(summary = "新增群組", description = "不用驗證，所有人都可以申請新群組")
	@PostMapping("/groups/new")
	public Group newGroup(@RequestBody ManageBean.NewGroupBean newGroupBean) {
		Group newGroup = Group.CreateServiceGroup(newGroupBean.getGroupName());
		newGroup.copyFromObject(newGroupBean);
		return cimsService.newGroup(newGroup);
	}
	@Operation(summary = "寄文字訊息給指定使用者", description = "該使用者必須是API KEY對應群組內成員，否則會驗證失敗")
	@PostMapping("/send/text")
	public MessageBean sendTextMessage(@RequestHeader(name = "Authorization") String accessToken,
									   @RequestBody ManageBean.SendBean sendBean) {
		if(!authorizationService.authorization(accessToken,"", User.CreateNoNameUserBean(sendBean.getInstantMessagingSoftware(),sendBean.getInstantMessagingSoftwareUserId())).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(cimsService.sendTextMessage(sendBean.getInstantMessagingSoftware(), sendBean.getInstantMessagingSoftwareUserId(), sendBean.getMessage()));
	}
	@Operation(summary = "搜尋群組", description = "透過keyword搜尋群組")
	@GetMapping("/groups/{keyword}/search")
	public List<Group.GroupData> searchGroup(@Parameter(description = "任意文字", example = "my group")
											 @PathVariable String keyword) {
		return cimsService.searchGroup(keyword);
	}
	@Operation(summary = "測試webhook", description = "會發送一個測試事件給該群組的webhook")
	@PostMapping("/groups/{groupId}/webhook/test")
	public MessageBean webhookTest(@RequestHeader(name = "Authorization") String accessToken,
								   @Parameter(description = "該群組的ID，由6位英數字組合的字串", example = "AbCd12")
								   @PathVariable String groupId){
		if(!authorizationService.authorization(accessToken,"",groupId).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(webhookService.testWebhook(groupId));
	}

	@Operation(summary = "群組詳細資料", description = "透過group id獲得該群組的詳細資料")
	@GetMapping("/groups/{groupId}/detail")
	public Group.GroupDetail groupDetail(@Parameter(description = "該群組的ID，由6位英數字組合的字串", example = "AbCd12")
										 @RequestParam String groupId) {
		return cimsService.groupDetail(groupId);
	}



	//put method
	@Operation(summary = "更改群組屬性", description = "groupId必填，其餘填修改項即可")
	@PutMapping("/groups/alter")
	public MessageBean alterGroup(@RequestHeader(name = "Authorization") String accessToken,
								  @RequestBody ManageBean.AlterGroupBean alterGroupBean) {
		if(!authorizationService.authorization(accessToken,"", alterGroupBean.getGroupId()).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		if(alterGroupBean.getGroupId()==null)
			return MessageBean.CreateMessageBean("The request body must have field \"groupId\"");
		Group alterGroup = Group.CreateEditGroup(alterGroupBean.getGroupId());
		return MessageBean.CreateMessageBean(cimsService.alterGroup(alterGroup.copyFromObject(alterGroupBean)));
	}
	@Operation(summary = "群組廣播文字訊息", description = "廣播文字訊息給所有成員")
	@PutMapping("/broadcast/text")
	public MessageBean broadcast(@RequestHeader(name = "Authorization") String accessToken,
								 @RequestBody ManageBean.BroadcastBean broadcastBean) {
		if(!authorizationService.authorization(accessToken,"", broadcastBean.getGroupId()).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(cimsService.broadcast(broadcastBean.getGroupId(), broadcastBean.getMessage()));
	}
	@Operation(summary = "賦予成員管理員權限", description = "必須用該群組的API KEY，否則會驗證失敗")
	@PutMapping("/permission/grant")
	public MessageBean grantPermission(@RequestHeader(name = "Authorization") String accessToken,
									   @RequestBody ManageBean.GrantPermissionBean grantPermissionBean) {
		if(!authorizationService.authorization(accessToken,"", grantPermissionBean.getGroupId()).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(cimsService.grantPermission(grantPermissionBean.getInstantMessagingSoftware(), grantPermissionBean.getInstantMessagingSoftwareUserId(), grantPermissionBean.getGroupId()));
	}
	@Operation(summary = "移除成員管理員權限", description = "必須用該群組的API KEY，否則會驗證失敗")
	@PutMapping("/permission/revoke")
	public MessageBean revokePermission(@RequestHeader(name = "Authorization") String accessToken,
										@RequestBody ManageBean.GrantPermissionBean grantPermissionBean) {
		if(!authorizationService.authorization(accessToken,"", grantPermissionBean.getGroupId()).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(cimsService.revokePermission(grantPermissionBean.getInstantMessagingSoftware(), grantPermissionBean.getInstantMessagingSoftwareUserId(), grantPermissionBean.getGroupId()));
	}

	//delete method
	@Operation(summary = "刪除群組", description = "不可逆操作，請小心使用")
	@DeleteMapping("/groups/{groupId}/delete")
	public MessageBean deleteGroup(@RequestHeader(name = "Authorization") String accessToken,
								   @Parameter(description = "該群組的ID，由6位英數字組合的字串", example = "AbCd12")
								   @PathVariable String groupId) {
		if(!authorizationService.authorization(accessToken,"",groupId).managerPermission())
			return MessageBean.CreateAuthorizationWrongMessageBean();
		return MessageBean.CreateMessageBean(cimsService.deleteGroup(groupId));
	}







}