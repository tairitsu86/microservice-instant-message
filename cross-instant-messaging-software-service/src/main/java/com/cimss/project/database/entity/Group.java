package com.cimss.project.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Table(name = "[GROUP]")
public class Group {
	@Schema(description = "該群組的ID，由32位英數字組合的字串",example = "0123a012345678b0123456c012345678")
	@Id
	@GeneratedValue(generator = "jpa-uuid")
	private String groupId;
	@Schema(description = "該群組的名字",example = "我ㄉ群組")
	@Column
	private String groupName;
	@Schema(description = "該群組的敘述",example = "這是我的群組")
	@Column
	private String groupDescription;
	@Schema(description = "該群組的Webhook",example = "https://myWebService/cimssWebhook")
	@Column
	private String groupWebhook;
	@Schema(description = "若文字訊息開頭符合關鍵字，將觸發Webhook寄送事件",example = "myService")
	@Column
	private String groupKeyword;
	@Schema(description = "該群組的API KEY",example = "1b619a98-55b4-4d80-8f50-e7aa9fde8cc5")
	@Column
	private String authorizationKey;
	@Schema(description = "該群組是否為公開(能被搜尋功能找到)群組?(預設true)",example = "true")
	@Column
	private boolean isPublic;
	@Schema(description = "其他使用者是否可以透過系統指令和group id加入此群組?(對API無影響)(預設true)",example = "true")
	@Column
	private boolean joinById;
	@Schema(description = "是否讓所有訊息皆被廣播?(預設false)",example = "false")
	@Column
	private boolean allMessageBroadcast;




	public static GroupData CreateDataBean(Group group){
		return new GroupData(group.groupId,group.groupName);
	}
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GroupData{
		@Schema(description = "該群組的ID，由32位英數字組合的字串",example = "0123a012345678b0123456c012345678")
		private String groupId;
		@Schema(description = "該群組的名字",example = "我ㄉ群組")
		private String groupName;
	}
	public static Group CreateServiceGroup(String groupName, String groupWebhook){
		return new Group(null,groupName,null,groupWebhook,null, UUID.randomUUID().toString(),true,true,false);
	}
	public static Group CreatePrivateGroup(String groupName){
		return new Group(null,groupName,null,null,null, UUID.randomUUID().toString(),false,true,true);
	}
}
