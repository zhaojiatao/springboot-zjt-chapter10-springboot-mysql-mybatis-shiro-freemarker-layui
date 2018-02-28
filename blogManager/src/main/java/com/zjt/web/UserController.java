package com.zjt.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zjt.entity.Tmenu;
import com.zjt.entity.Trole;
import com.zjt.entity.Tuser;
import com.zjt.service.TmenuService;
import com.zjt.service.TroleService;
import com.zjt.service.TuserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @Author: Zhaojiatao
* @Description: 当前登录用户控制器
* @Date: Created in 2018/2/8 19:28
* @param 
*/
@Controller
@RequestMapping("/user")
public class UserController {

	@Resource
	private TroleService troleService;
	
	@Resource
	private TuserService tuserService;
	
	@Resource
	private TmenuService tmenuService;
	
	/*@Resource
	private LogService logService;*/
	
	/**
     * 用户登录请求
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Map<String,Object> login(String imageCode, @Valid Tuser user, BindingResult bindingResult, HttpSession session){
    	Map<String,Object> map=new HashMap<String,Object>();
    	if(StringUtils.isEmpty(imageCode)){
    		map.put("success", false);
    		map.put("errorInfo", "请输入验证码！");
    		return map;
    	}
    	if(!session.getAttribute("checkcode").equals(imageCode)){
    		map.put("success", false);
    		map.put("errorInfo", "验证码输入错误！");
    		return map;
    	}
    	if(bindingResult.hasErrors()){
    		map.put("success", false);
    		map.put("errorInfo", bindingResult.getFieldError().getDefaultMessage());
    		return map;
    	}
		Subject subject=SecurityUtils.getSubject();
		UsernamePasswordToken token=new UsernamePasswordToken(user.getUserName(), user.getPassword());
		try{
			subject.login(token); // 登录认证
			String userName=(String) SecurityUtils.getSubject().getPrincipal();
			//Tuser currentUser=tuserService.findByUserName(userName);
			Example tuserExample=new Example(Tuser.class);
			tuserExample.or().andEqualTo("userName",userName);
			Tuser currentUser=tuserService.selectByExample(tuserExample).get(0);
			session.setAttribute("currentUser", currentUser);
			//List<Trole> roleList=troleService.findByUserId(currentUser.getId());
			List<Trole> roleList=troleService.selectRolesByUserId(currentUser.getId());
			map.put("roleList", roleList);
			map.put("roleSize", roleList.size());
			map.put("success", true);
			//logService.save(new Log(Log.LOGIN_ACTION,"用户登录")); // 写入日志
			return map;
		}catch(Exception e){
			e.printStackTrace();
			map.put("success", false);
			map.put("errorInfo", "用户名或者密码错误！");
			return map;
		}
    }



	/**
	 * 保存角色信息
	 * @param roleId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/saveRole")
	public Map<String,Object> saveRole(Integer roleId,HttpSession session)throws Exception{
		Map<String,Object> map=new HashMap<String,Object>();
		Trole currentRole=troleService.selectByKey(roleId);
		session.setAttribute("currentRole", currentRole); // 保存当前角色信息

		putTmenuOneClassListIntoSession(session);

		map.put("success", true);
		return map;
	}


	/**
	 * 安全退出
	 *
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/logout")
	public String logout() throws Exception {
//		logService.save(new Log(Log.LOGOUT_ACTION,"用户注销"));
		SecurityUtils.getSubject().logout();
		return "redirect:/tologin";
	}


	/**
	 * 加载权限菜单
	 * @param session
	 * @return
	 * @throws Exception
	 * 这里传入的parentId是1
	 */
	@ResponseBody
	@GetMapping("/loadMenuInfo")
	public String loadMenuInfo(HttpSession session, Integer parentId)throws Exception{

		putTmenuOneClassListIntoSession(session);

		Trole currentRole=(Trole) session.getAttribute("currentRole");
		//根据当前用户的角色id和父节点id查询所有菜单及子集json
		String json=getAllMenuByParentId(parentId,currentRole.getId()).toString();
		//System.out.println(json);
		return json;
	}

	/**
	 * 获取根频道所有菜单信息
	 * @param parentId
	 * @param roleId
	 * @return
	 */
	private JsonObject getAllMenuByParentId(Integer parentId,Integer roleId){
		JsonObject resultObject=new JsonObject();
		JsonArray jsonArray=this.getMenuByParentId(parentId, roleId);//得到所有一级菜单
		for(int i=0;i<jsonArray.size();i++){
			JsonObject jsonObject=(JsonObject) jsonArray.get(i);
			//判断该节点下时候还有子节点
			Example example=new Example(Tmenu.class);
			example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
			//if("true".equals(jsonObject.get("spread").getAsString())){
			if (tmenuService.selectCountByExample(example)==0) {
				continue;
			}else{
				//由于后台模板的规定，一级菜单以title最为json的key
				resultObject.add(jsonObject.get("title").getAsString(), getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(),roleId));
			}
		}
		return resultObject;
	}



	//获取根频道下子频道菜单列表集合
	private JsonArray getAllMenuJsonArrayByParentId(Integer parentId,Integer roleId){
		JsonArray jsonArray=this.getMenuByParentId(parentId, roleId);
		for(int i=0;i<jsonArray.size();i++){
			JsonObject jsonObject=(JsonObject) jsonArray.get(i);
			//判断该节点下是否还有子节点
			Example example=new Example(Tmenu.class);
			example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
			//if("true".equals(jsonObject.get("spread").getAsString())){
			if (tmenuService.selectCountByExample(example)==0) {
				continue;
			}else{
				//二级或三级菜单
				jsonObject.add("children", getAllMenuJsonArrayByParentId(jsonObject.get("id").getAsInt(),roleId));
			}
		}
		return jsonArray;
	}




	/**
	 * 根据父节点和用户角色id查询菜单
	 * @param parentId
	 * @param roleId
	 * @return
	 */
	private JsonArray getMenuByParentId(Integer parentId,Integer roleId){
		//List<Menu> menuList=menuService.findByParentIdAndRoleId(parentId, roleId);
		HashMap<String,Object> paraMap=new HashMap<String,Object>();
		paraMap.put("pid",parentId);
		paraMap.put("roleid",roleId);
		List<Tmenu> menuList=tmenuService.selectByParentIdAndRoleId(paraMap);
		JsonArray jsonArray=new JsonArray();
		for(Tmenu menu:menuList){
			JsonObject jsonObject=new JsonObject();
			jsonObject.addProperty("id", menu.getId()); // 节点id
			jsonObject.addProperty("title", menu.getName()); // 节点名称
			jsonObject.addProperty("spread", false); // 不展开
			jsonObject.addProperty("icon", menu.getIcon());
			if(StringUtils.isNotEmpty(menu.getUrl())){
				jsonObject.addProperty("href", menu.getUrl()); // 菜单请求地址
			}
			jsonArray.add(jsonObject);

		}
		return jsonArray;
	}




public void putTmenuOneClassListIntoSession(HttpSession session){
	//用来在welcome.ftl中获取主菜单列表
	Example example=new Example(Tmenu.class);
	example.or().andEqualTo("pId",1);
	List<Tmenu> tmenuOneClassList=tmenuService.selectByExample(example);
	session.setAttribute("tmenuOneClassList", tmenuOneClassList);
}











    
}
