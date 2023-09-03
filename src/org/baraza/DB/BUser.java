/**
 * @author      Dennis W. Gichangi <dennis@openbaraza.org>
 * @version     2011.0329
 * @since       1.6
 * website		www.openbaraza.org
 * The contents of this file are subject to the GNU Lesser General Public License
 * Version 3.0 ; you may use this file in compliance with the License.
 */
package org.baraza.DB;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.baraza.xml.BElement;

public class BUser {
	Integer orgID = null;
	String orgName = "";
	String webLogos = "";
	
	Integer languageId = null;
	String userIP = null;
	String userName = null;
	String userID = null;
	String groupID = null;
	String entityName = null;
	String entityType = null;
	String startView = null;
	String groupIDs = null;
	List<String> groupRole;
	List<String> userRole;
	List<String> accessLevels;
	boolean nullUser = false;
	boolean superUser = false;
	boolean departmentFilter = false;
	boolean deploymentFilter = false;
	String entityDepartments = null;
	String entityDeployments = null;
	String logoPath = null;
	
	public BUser(BDB db, String userIP, String userName, boolean newUser) {
		this.userIP = userIP;
		this.userName = userName;
		groupRole = new ArrayList<String>();
		userRole = new ArrayList<String>();
		accessLevels = new ArrayList<String>();
		
		userID = "0";
		entityName = "root";
		entityType = "0";
		nullUser = true;
		
		orgID = 0;
		languageId = 0;
		orgName = "default";
		webLogos = "";
	}
	
	public BUser(BDB db, String userIP, String userName, String userId) {
		makeUser(db, userIP, userName);
		
		String insSql = "INSERT INTO sys_logins (entity_id, login_ip) VALUES ('"
		+ userId + "', '" + userIP + "')";
		db.executeQuery(insSql);
	}

	public BUser(BDB db, String userIP, String userName) {
		makeUser(db, userIP, userName);
	}
	
	public void makeUser(BDB db, String userIP, String userName) {
		this.userIP = userIP;
		this.userName = userName;
		groupRole = new ArrayList<String>();
		userRole = new ArrayList<String>();
		accessLevels = new ArrayList<String>();

		String mySql = "SELECT entity_id, entity_type_id, org_id, sys_language_id, no_org, "
			+ "entity_name, super_user, entity_leader, function_role "
			+ "FROM entitys WHERE user_name = '" + userName + "'";
		BQuery rs = new BQuery(db, mySql);

		if(rs.moveNext()) {
			entityName = rs.readField("entity_name");
			entityType = rs.readField("entity_type_id");
			userID = rs.readField("entity_id");
			groupID = rs.readField("entity_type_id");
			orgID = rs.getInt("org_id");
			if(rs.getBoolean("no_org")) orgID = null;
			
			if(rs.readField("sys_language_id") == null) languageId = 0;
			else languageId = rs.getInt("sys_language_id");

			superUser = rs.getBoolean("super_user");
			String functionRole = rs.readField("function_role");
			if(functionRole != null) {
				String functionRoles[] = functionRole.split(",");
				userRole = Arrays.asList(functionRoles);
			}
			
			mySql = "SELECT sys_access_levels.sys_access_level_name, sys_access_levels.access_tag "
				+ "FROM sys_access_levels INNER JOIN sys_access_entitys ON sys_access_levels.sys_access_level_id = sys_access_entitys.sys_access_level_id "
				+ "WHERE sys_access_entitys.entity_id = " + userID
				+ " AND sys_access_entitys.org_id = " + orgID;
			BQuery alRs = new BQuery(db, mySql);
			while(alRs.moveNext()) accessLevels.add(alRs.readField("access_tag").trim());
			alRs.close();
		} else {
			userID = "0";
		}
		rs.close();

		startView = db.executeFunction("SELECT start_view FROM entity_types WHERE entity_type_id = " + entityType);
		if(startView == null) startView = "1:0";

		if(orgID != null) {
			String wlSql = "SELECT org_name, department_filter, logo_path, "
				+ "(CASE WHEN web_logos = true THEN '/' || org_id::text ELSE '' END) AS web_logo_path "
				+ "FROM orgs WHERE org_id = " + orgID;
			BQuery rsOrg = new BQuery(db, wlSql);
			if(rsOrg.moveNext()) {
				orgName = rsOrg.getString("org_name");
				departmentFilter = rsOrg.getBoolean("department_filter");
				logoPath = rsOrg.getString("logo_path");
				webLogos = rsOrg.getString("web_logo_path");
			}
			rsOrg.close();
			
			wlSql = "SELECT column_name FROM information_schema.columns WHERE table_name='orgs' and column_name='deployment_filter';";
			if(db.executeFunction(wlSql) != null) {
				wlSql = "SELECT deployment_filter FROM orgs WHERE org_id = " + orgID;
				rsOrg = new BQuery(db, wlSql);
				if(rsOrg.moveNext()) deploymentFilter = rsOrg.getBoolean("deployment_filter");
				rsOrg.close();
			}
		}

		if(userID != null) {
			mySql = "SELECT entity_types.entity_type_id, entity_types.entity_role "
				+ "FROM entity_types INNER JOIN entity_subscriptions ON entity_types.entity_type_id = Entity_subscriptions.entity_type_id "
				+ "WHERE entity_subscriptions.entity_id = '" + userID + "'";
			BQuery rsRole = new BQuery(db, mySql);
			while(rsRole.moveNext()) {
				groupRole.add(rsRole.readField("entity_role"));
				if(groupIDs == null) groupIDs = rsRole.readField("entity_type_id");
				else groupIDs += "," + rsRole.readField("entity_type_id");
			}
			rsRole.close();
		
			if(departmentFilter) {
				String edSql = "SELECT string_agg(department_id::text, ',') "
					+ "FROM entity_departments WHERE entity_id=" + userID;
				entityDepartments = db.executeFunction(edSql);
				if(entityDepartments == null) entityDepartments = "-1";
			}
			
			if(deploymentFilter) {
				String edSql = "SELECT string_agg(deployment_id::text, ',') "
					+ "FROM entity_deployments WHERE entity_id = " + userID;
				entityDeployments = db.executeFunction(edSql);
				if(entityDeployments == null) entityDepartments = "-1";
			}
		}

		if(groupIDs == null) groupIDs = "";
		if(userID == null) userID = "0";
	}
	
	public boolean checkAccess(String role, String access) {
		if((role == null) && (access == null)) return true;
		
		boolean hasAccess  = false;
		if(superUser) {
			hasAccess = true;
		} else if(role != null) {
			String mRoles[] = role.split(",");
			for(String mRole : mRoles) {
				if(userRole.contains(mRole)) hasAccess = true;
			}
		} else if(access != null) {
			if(accessLevels.contains(access)) hasAccess = true;
		}
		
		return hasAccess;
	}
	
	public int checkRole(BElement mel, String deskKey) {
		int toShow = 0;

		//System.out.println("BASE 2010 : " + mel.getAttribute("name"));
		
		// If its a super user grant authority
		if(superUser) return 1;

		for(BElement smel: mel.getElements()) {
						
			if(toShow == 0) {
				if(smel.isLeaf()) {
					if(deskKey.equals(smel.getValue())) {
						boolean hasAccess  = checkAccess(smel.getAttribute("role"), smel.getAttribute("access"));
						if(hasAccess) return 1;
						else return 2;
					}
				} else {
					toShow = checkRole(smel, deskKey);
					if(toShow != 0) {
						boolean hasAccess  = checkAccess(smel.getAttribute("role"), smel.getAttribute("access"));
						if(hasAccess) return toShow;
						else return 2;
					}
				}
			}
		}
		
		return toShow;
	}

	
	public String insAudit(String tableName, String recordID, String functionType) {
		String inssql = "INSERT INTO sys_audit_trail (user_id, user_ip, table_name, record_id, change_type) VALUES('";
		inssql += getUserID() + "', '" + getUserIP() + "', '" + tableName + "', '" + recordID  + "', '" + functionType + "')";
		return inssql;
	}

	public void setUser(BDB db, String tableName, String idCol, String nameCol, String userName) {
		String mysql = "SELECT " + idCol + " FROM " +  tableName;
		mysql += " WHERE " + nameCol + " = '" + userName + "'";

		BQuery rs = new BQuery(db, mysql);
		if(rs.moveNext()) userID = rs.readField(idCol);

		if(userID == null) userID = "0";
	}

	public String getOrgWhere(String orgTable) {
		String ow = "";
		if(orgTable == null) orgTable = "";
		else orgTable = orgTable + ".";
		if(orgID != null) {
			ow = " WHERE (" + orgTable + "org_id = " + orgID + ")";
		}
		return ow;
	}

	public String getOrgAnd(String orgTable) {
		String ow = "";
		if(orgTable == null) orgTable = "";
		else orgTable = orgTable + ".";
		if(orgID != null) {
			ow = " AND (" + orgTable + "org_id = " + orgID + ")";
		}
		return ow;
	}
	
	public String getDeptSql(String orgTable) {
		if(entityDepartments == null) return "";
		if(orgTable == null) orgTable = "";
		else orgTable = orgTable + ".";
		return " AND (" + orgTable + "department_id IN (" + entityDepartments + "))";
	}
	
	public String getDeplSql(String orgTable) {
		if(entityDeployments == null) return "";
		if(orgTable == null) orgTable = "";
		else orgTable = orgTable + ".";
		return " AND (" + orgTable + "deployment_id IN (" + entityDeployments + "))";
	}
	
	public String getUserID() { return userID; }
	public String getUserIP() { return userIP; }
	public String getUserName() { return userName; }
	public String getEntityName() { return entityName; }
	public String getStartView() { return startView; }
	public boolean getSuperUser() { return superUser; }
	public boolean getNullUser() { return nullUser; }
	public List<String> getUserRoles() { return userRole; }
	public List<String> getGroupRoles() { return groupRole; }
	public List<String> getAccessLevels() { return accessLevels; }
	public String getGroupID() { return groupID; }
	public String getGroupIDs() { return groupIDs; }
	public String getEntityDepartments() { return entityDepartments; }
	public String getEntityDeployments() { return entityDeployments; }
	public String getLogoPath() { return logoPath; }

	public Integer getUserOrgId() { return orgID; }
	public String getUserOrg() { 
		if(orgID == null) return null;
		return orgID.toString(); 
	}
	public String getUserOrgName() { return orgName; }
	public Integer getLanguageId() { return languageId; }
	public String getWebLogos() { return webLogos; }

}
