package com.zjt.web;

import com.github.pagehelper.PageHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zjt.entity.Tmenu;
import com.zjt.entity.Trole;
import com.zjt.entity.Trolemenu;
import com.zjt.entity.Tuserrole;
import com.zjt.model.JqgridBean;
import com.zjt.model.PageRusult;
import com.zjt.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 后台管理用户Controller
         *
         * @author zjt
        */
@Controller
@RequestMapping("/admin/role")
public class RoleAdminController {

    @Resource
    private TuserService userService;

    @Resource
    private TroleService roleService;

    @Resource
    private TuserroleService userRoleService;

    @Resource
    private TmenuService tmenuService;

    @Resource
    private TrolemenuService trolemenuService;


    @RequestMapping("/torolemanage")
    @RequiresPermissions(value = {"角色管理"})
    public String tousermanage() {
        return "power/role";
    }

    /**
     * 分页查询角色信息
     */

    @ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> list(JqgridBean jqgridbean
                    /*String userName,@RequestParam(value="page",required=false)Integer page*/
    ) throws Exception {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> datamap = new LinkedHashMap<String, Object>();

        Example troleExample = new Example(Trole.class);
        //tuserExample.or().andIdNotEqualTo(1L);
        Example.Criteria criteria = troleExample.or();

        if (StringUtils.isNotEmpty(jqgridbean.getSearchField())) {
            if ("name".equalsIgnoreCase(jqgridbean.getSearchField())) {
                if ("eq".contentEquals(jqgridbean.getSearchOper())) {
                    criteria.andEqualTo("name",jqgridbean.getSearchString());
                }
            }
        }

        PageHelper.startPage(jqgridbean.getPage(), jqgridbean.getLength());
        List<Trole> roleList = roleService.selectByExample(troleExample);
        PageRusult<Trole> pageRusult =new PageRusult<Trole>(roleList);

        /*Integer totalrecords = roleService.countByExample(troleExample);//总记录数
        Page pagebean = new Page(jqgridbean.getLength() * ((jqgridbean.getPage() > 0 ? jqgridbean.getPage() : 1) - 1), jqgridbean.getLength(), totalrecords);
        troleExample.setPage(pagebean);
        troleExample.setOrderByClause(jqgridbean.getSidx() + " " + jqgridbean.getSord());
        List<Trole> roleList = roleService.selectByExample(troleExample);*/

        resultmap.put("currpage", String.valueOf(pageRusult.getPageNum()));
        resultmap.put("totalpages", String.valueOf(pageRusult.getPages()));
        resultmap.put("totalrecords", String.valueOf(pageRusult.getTotal()));
        resultmap.put("datamap", roleList);

        return resultmap;
    }


    @ResponseBody
    @RequestMapping(value = "/addupdaterole")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> addupdaterole(Trole trole) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if (trole.getId() == null) {//新建
                //首先判断用户名是否可用
                Example troleExample = new Example(Trole.class);
                troleExample.or().andEqualTo("name",trole.getName());
                List<Trole> rolelist = roleService.selectByExample(troleExample);
                if (rolelist != null && rolelist.size() > 0) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前角色名已存在");
                    return resultmap;
                }
                roleService.saveNotNull(trole);
            } else {//编辑
                Trole oldObject = roleService.selectByKey(trole.getId());
                if (oldObject == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "当前角色名不存在");
                    return resultmap;
                } else {
                    roleService.updateNotNull(trole);
                }
            }
            resultmap.put("state", "success");
            resultmap.put("mesg", "操作成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }


    @ResponseBody
    @RequestMapping(value = "/deleterole")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> deleteuser(Trole trole) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if (trole.getId() != null && !trole.getId().equals(0)) {
                Trole role = roleService.selectByKey(trole.getId());
                if (role == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "删除失败,无法找到该记录");
                    return resultmap;
                } else {
                    //还需删除用户角色中间表
                    Example tuserroleexample = new Example(Tuserrole.class);
                    tuserroleexample.or().andEqualTo("roleId",trole.getId());
                    userRoleService.deleteByExample(tuserroleexample);
                    Example trolemenuexample = new Example(Trolemenu.class);
                    trolemenuexample.or().andEqualTo("roleId",trole.getId());
                    trolemenuService.deleteByExample(trolemenuexample);

                    roleService.delete(trole.getId());
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }


            resultmap.put("state", "success");
            resultmap.put("mesg", "删除成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
    }


    @ResponseBody
    @RequestMapping(value = "/selectRoleById")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> selectRoleById(Trole trole) {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
        try {
            if (trole.getId() != null && !trole.getId().equals(0)) {
                trole = roleService.selectByKey(trole.getId());
                if (trole == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }

            resultmap.put("trole", trole);
            resultmap.put("state", "success");
            resultmap.put("mesg", "获取成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "获取失败，系统异常");
            return resultmap;
        }
    }


    /**
     * 根据父节点获取所有复选框权限菜单树
     *
     * @param parentId
     * @param roleId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/loadCheckMenuInfo")
    @RequiresPermissions(value = {"角色管理"})
    public String loadCheckMenuInfo(Integer parentId, Integer roleId) throws Exception {
        List<Tmenu> menuList = tmenuService.selectMenusByRoleId(roleId);// 根据角色查询所有权限菜单信息
        //移除所有没有pid的menuid
        Iterator<Tmenu> it = menuList.iterator();
        while (it.hasNext()) {
            Tmenu tmenu = it.next();
            if (tmenu.getpId() == null) {
                it.remove();
            }
        }
        List<Integer> menuIdList = new LinkedList<Integer>();
        for (Tmenu menu : menuList) {
            menuIdList.add(menu.getId());
        }
        String json = getAllCheckedMenuByParentId(parentId, menuIdList).toString();
        //System.out.println(json);
        return json;
    }

    /**
     * 根据父节点ID和权限菜单ID集合获取复选框菜单节点
     *
     * @param parentId
     * @param menuIdList
     * @return
     */
    private JsonArray getAllCheckedMenuByParentId(Integer parentId, List<Integer> menuIdList) {
        JsonArray jsonArray = this.getCheckedMenuByParentId(parentId, menuIdList);
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            //判断该节点下时候还有子节点
            Example example=new Example(Tmenu.class);
            example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
            //if ("open".equals(jsonObject.get("state").getAsString())) {
            if (tmenuService.selectCountByExample(example)==0) {
                continue;
            } else {
                jsonObject.add("children", getAllCheckedMenuByParentId(jsonObject.get("id").getAsInt(), menuIdList));
            }
        }
        return jsonArray;
    }

    /**
     * 根据父节点ID和权限菜单ID集合获取复选框菜单节点
     *
     * @param parentId
     * @param menuIdList
     * @return
     */
    private JsonArray getCheckedMenuByParentId(Integer parentId, List<Integer> menuIdList) {
        Example tmenuExample = new Example(Tmenu.class);
        tmenuExample.or().andEqualTo("pId",parentId);
        List<Tmenu> menuList = tmenuService.selectByExample(tmenuExample);
        JsonArray jsonArray = new JsonArray();
        for (Tmenu menu : menuList) {
            JsonObject jsonObject = new JsonObject();
            Integer menuId = menu.getId();
            jsonObject.addProperty("id", menuId); // 节点id
            jsonObject.addProperty("name", menu.getName()); // 节点名称
            //判断该节点下时候还有子节点
            Example example=new Example(Tmenu.class);
            example.or().andEqualTo("pId",jsonObject.get("id").getAsString());
            //if (menu.getState() == 1) {
            if (tmenuService.selectCountByExample(example)==0) {
                jsonObject.addProperty("open", "true"); // 无子节点
            } else {
                jsonObject.addProperty("open", "false"); // 有子节点
            }
            if (menuIdList.contains(menuId)) {
                jsonObject.addProperty("checked", true);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 保存角色权限设置
     *
     * @param menuIds
     * @param roleId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/saveMenuSet")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> saveMenuSet(String menuIds, Integer roleId) throws Exception {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();

        if (StringUtils.isNotEmpty(menuIds)) {
            //先根据roleid查询出原有的对应的所有menuid集合
            List<Tmenu> menuList = tmenuService.selectMenusByRoleId(roleId);
            //移除所有没有pid的menuid
            Iterator<Tmenu> it = menuList.iterator();
            while (it.hasNext()) {
                Tmenu tmenu = it.next();
                if (tmenu.getpId() == null) {
                    it.remove();
                }
            }
            List<Integer> menuIdList = new LinkedList<Integer>();
            for (Tmenu menu : menuList) {
                menuIdList.add(menu.getId());
            }

            if(menuIdList!=null&&menuIdList.size()>0){
                Example trolemenuExample = new Example(Trolemenu.class);
                trolemenuExample.or().andEqualTo("roleId",roleId).andIn("menuId",menuIdList);
                trolemenuService.deleteByExample(trolemenuExample);
            }

            String idsStr[] = menuIds.split(",");
            for (int i = 0; i < idsStr.length; i++) { // 然后添加所有角色权限关联实体
                Trolemenu trolemenu = new Trolemenu();
                trolemenu.setRoleId(roleId);
                trolemenu.setMenuId(Integer.parseInt(idsStr[i]));
                trolemenuService.saveNotNull(trolemenu);
            }
        }else{
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，未获取选中记录，请重新选择");
            return resultmap;
        }
        resultmap.put("state", "success");
        resultmap.put("mesg", "操作成功");
        return resultmap;
    }


    /*//加载细粒度字段级别权限树
    @ResponseBody
    @PostMapping("/loadColumnsPermissionTree")
    @RequiresPermissions(value = {"角色管理"})
    public String loadColumnsPermissionTree(Integer roleId) throws Exception {
        List<Tmenu> menuList = tmenuService.selectMenusByRoleId(roleId);// 根据角色查询所有权限菜单信息
        //移除有pid的menuid
        Iterator<Tmenu> it = menuList.iterator();
        while (it.hasNext()) {
            Tmenu tmenu = it.next();
            if (tmenu.getPid() != null) {
                it.remove();
            }
        }
        List<String> menuNameList = new LinkedList<String>();
        for (Tmenu menu : menuList) {
            menuNameList.add(menu.getName());
        }

        JsonArray jsonArray = new JsonArray();

        for (XyStates xystate : XyStates.values()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", xystate.toString()); // 节点名称
            jsonObject.addProperty("checked", "false");
            jsonObject.addProperty("chkDisabled", "true");
            jsonObject.addProperty("nocheck", "true");

            JsonArray jsonArray2 = new JsonArray();
            for (TableNames tablename : TableNames.values()) {
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", tablename.toString()); // 节点名称
                jsonObject2.addProperty("checked", "false");
                jsonObject2.addProperty("chkDisabled", "true");
                jsonObject2.addProperty("nocheck", "true");
                JsonArray jsonArray3 = new JsonArray();

                Object[] columnObject = null;
                switch (tablename.toString()) {
                    case "FCJY_CLF_ZJJGXY":
                        columnObject = FcjyClfZjjgxyColumns.values();
                        break;
                    case "FCJY_CLF_ZJJGHT":
                        columnObject = FcjyClfZjjghtColumns.values();
                        break;
                    case "FCJY_CLF_ZJJGHT_ZT":
                        columnObject = FcjyClfZjjghtZtColumns.values();
                        break;
                    case "FCJY_CLF_ZJJGZH":
                        columnObject = FcjyClfZjjgzhColumns.values();
                        break;
                    case "FCJY_CLF_ZJJGRZJL":
                        columnObject = FcjyClfZjjgrzjlColumns.values();
                        break;
                    case "FCJY_CLF_ZJJGCZJL":
                        columnObject = FcjyClfZjjgczjlColumns.values();
                        break;
                    default:
                        break;
                }

                for (Object column : columnObject) {
                    JsonObject jsonObject3 = new JsonObject();
                    jsonObject3.addProperty("name", column.toString()); // 节点名称
                    jsonObject3.addProperty("checked", "false");
                    jsonObject3.addProperty("chkDisabled", "false");
                    jsonObject3.addProperty("nocheck", "true");
                    JsonArray jsonArray4 = new JsonArray();

                    for (TableOptions option : TableOptions.values()) {
                        JsonObject jsonObject4 = new JsonObject();
                        jsonObject4.addProperty("name", option.toString()); // 节点名称
                        String targetname = (xystate.toString() + ":" + tablename.toString() + ":" + column.toString() + ":" + option.toString()).trim();
                        if (menuNameList.contains(targetname)) {
                            jsonObject4.addProperty("checked", "true");
                        } else {
                            jsonObject4.addProperty("checked", "false");
                        }
                        jsonObject4.addProperty("id", targetname);
                        jsonObject3.addProperty("chkDisabled", "false");
                        jsonArray4.add(jsonObject4);
                    }

                    jsonObject3.add("children", jsonArray4);
                    jsonArray3.add(jsonObject3);
                }


                jsonObject2.add("children", jsonArray3);
                jsonArray2.add(jsonObject2);
            }

            jsonObject.add("children", jsonArray2);
            jsonArray.add(jsonObject);
        }

        System.out.println(jsonArray.toString());

        return jsonArray.toString();
    }*/


    /**
     * 保存角色字段权限设置
     *
     * @param permisIds
     * @param roleId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/saveColumnsSet")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> saveColumnsSet(String permisIds, Integer roleId) throws Exception {
        LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();

        if (StringUtils.isNotEmpty(permisIds.trim())) {

            //先根据roleid查询出原有的对应的所有menuid集合
            List<Tmenu> menuList = tmenuService.selectMenusByRoleId(roleId);
            //移除所有没有pid的menuid
            Iterator<Tmenu> it = menuList.iterator();
            while (it.hasNext()) {
                Tmenu tmenu = it.next();
                if (tmenu.getpId() != null) {
                    it.remove();
                }
            }
            List<Integer> menuIdList = new LinkedList<Integer>();
            for (Tmenu menu : menuList) {
                menuIdList.add(menu.getId());
            }

            if(menuIdList!=null&&menuIdList.size()>0){
                Example trolemenuExample = new Example(Trolemenu.class);
                trolemenuExample.or().andEqualTo("roleId",roleId).andIn("menuId",menuIdList);
                trolemenuService.deleteByExample(trolemenuExample);
            }




            String namesStr[] = permisIds.split(",");
            for (int i = 0; i < namesStr.length; i++) { // 然后添加所有角色权限关联实体
                if (StringUtils.isEmpty(namesStr[i])) {
                    continue;
                }
                Integer menuid = null;
                //根据name先查询有没有这个menu
                Example tmenuExample = new Example(Tmenu.class);
                tmenuExample.or().andEqualTo("name",namesStr[i]).andIsNull("pId");
                List<Tmenu> tmenuList = tmenuService.selectByExample(tmenuExample);
                if (tmenuList == null || tmenuList.size() == 0) {
                    //当前menu表还没有这个name的记录，则先新增menu表的记录，再新增中间表的几率
                    Tmenu tmenu = new Tmenu();
                    tmenu.setName(namesStr[i]);
                    tmenuService.saveNotNull(tmenu);

                    Example tmenuExample2 = new Example(Tmenu.class);
                    tmenuExample2.or().andEqualTo("name",tmenu.getName()).andIsNull("pId");
                    List<Tmenu> tmenuList2 = tmenuService.selectByExample(tmenuExample2);
                    if (tmenuList2 != null && tmenuList2.size() > 0) {
                        menuid = tmenuList2.get(0).getId();
                    }
                } else {
                    menuid = tmenuList.get(0).getId();
                }

                Trolemenu trolemenu = new Trolemenu();
                trolemenu.setRoleId(roleId);
                trolemenu.setMenuId(menuid);
                trolemenuService.saveNotNull(trolemenu);
            }
        } else {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "未获取选中记录,请重新选择");
            return resultmap;
        }
        resultmap.put("state", "success");
        resultmap.put("mesg", "操作成功");
        return resultmap;
    }


}
