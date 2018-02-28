<!DOCTYPE html>
<html class="loginHtml">
<head>
	<meta charset="utf-8">
	<title>登录-后台管理系统</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="format-detection" content="telephone=no">
	<link rel="icon" href="${basePath!}/static/logo.png">
	<link rel="stylesheet" href="${basePath!}/static/layui/css/layui.css" media="all" />
	<link rel="stylesheet" href="${basePath!}/static/css/public.css" media="all" />
</head>
<body class="loginBody">
	<form class="layui-form">
		<div class="login_face"><img src="${basePath!}/static/logo.png" class="userAvatar"></div>
		<div class="layui-form-item input-item">
			<label for="userName">用户名</label>
			<input type="text" placeholder="请输入用户名" autocomplete="off" id="userName" name="userName" class="layui-input" lay-verify="required|userName">
		</div>
		<div class="layui-form-item input-item">
			<label for="password">密码</label>
			<input type="password" placeholder="请输入密码" autocomplete="off" id="password"  name="password"  class="layui-input" lay-verify="required|password">
		</div>
		<div class="layui-form-item input-item" id="imgCode">
			<label for="code">验证码</label>
			<input type="text" placeholder="请输入验证码" id="imageCode" name="imageCode"  autocomplete="off" id="code" class="layui-input">
			<#--验证码通过接口获取-->
            <img id="imgObj" title="看不清，换一张" src="${basePath!}/drawImage" onclick="changeImg()"/>
		</div>
		<div class="layui-form-item">
			<button class="layui-btn layui-block" lay-filter="login" lay-submit>登录</button>
		</div>
		<#--<div class="layui-form-item layui-row">
			<a href="javascript:;" class="seraph icon-qq layui-col-xs4 layui-col-sm4 layui-col-md4 layui-col-lg4"></a>
			<a href="javascript:;" class="seraph icon-wechat layui-col-xs4 layui-col-sm4 layui-col-md4 layui-col-lg4"></a>
			<a href="javascript:;" class="seraph icon-sina layui-col-xs4 layui-col-sm4 layui-col-md4 layui-col-lg4"></a>
		</div>-->
	</form>
	<script type="text/javascript" src="${basePath!}/static/layui/layui.js"></script>
	<script type="text/javascript" src="${basePath!}/static/page/login/login.js"></script>
	<script type="text/javascript" src="${basePath!}/static/js/cache.js"></script>
    <script type="text/javascript" src="${basePath!}/static/js/verifyCode.js"></script>



    <script>
        layui.use(['jquery', 'layer', 'form'], function () {
            var layer = layui.layer,
                    $ = layui.jquery,
                    form = layui.form;

            //监听表单提交事件
            form.on('submit(login)', function (data) {

                $.post("${basePath!}/user/login", {
                    userName: $("#userName").val(),
                    password: $("#password").val(),
                    imageCode: $("#imageCode").val()
                }, function (result) {
                    if (result.success) {
                        if (result.roleSize == 1) {
                            var roleId = result.roleList[0].id;
                            $.post("${basePath!}/user/saveRole", {roleId: roleId}, function (result) {
                                if (result.success) {
                                    window.location.href = "${basePath!}/welcome";
                                }
                            });
                        } else {
                            $("#roleList").empty();
                            var roles = result.roleList;
                            for (var i = 0; i < roles.length; i++) {
                                if (i == 0) {
                                    $("#roleList").append("<input type='radio' checked=true  name='role' title='" + roles[i].name + "' value='" + roles[i].id + "'/>")

                                } else {
                                    $("#roleList").append("<input type='radio' name='role'  title='" + roles[i].name + "' value='" + roles[i].id + "'/>")
                                }
                                layui.form.render();//刷新所有表单的渲染效果
                            }

                            layer.open(
                                    {
                                        type: 1,
                                        title: '请选择一个角色登录系统',
                                        content: $("#light"),
                                        area: '500px',
                                        offset: 'auto',
                                        skin: 'layui-layer-molv',
                                        shade: [0.8, '#393D49']
                                    }
                            )

                            /*document.getElementById('light').style.display='block';
                            document.getElementById('fade').style.display='block';*/
                        }
                    } else {
                        layer.alert(result.errorInfo);
                    }
                });


                return false;
            });


            //监听角色选择提交
            form.on('submit(choserolefilter)', function (data) {
                saveRole();
                return false;
            });

        });


        function saveRole() {
            var roleId = $("input[name='role']:checked").val();
            $.post("${basePath!}/user/saveRole", {roleId: roleId}, function (result) {
                if (result.success) {
                    window.location.href = "${basePath!}/welcome";
                }
            });
        }


    </script>



    <div id="light" hidden class="layui-fluid">

        <form class="layui-form" action="" style="padding: inherit;width: inherit;height: inherit;position: static;margin: auto;box-shadow: none">
            <div class="layui-form-item">
                <label class="layui-form-label">请选择角色</label>
                <div class="layui-input-block" id="roleList">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit lay-filter="choserolefilter">确定</button>
                </div>
            </div>
        </form>
    </div>






</body>
</html>


<script type="text/javascript">
    /*session过期后登陆页面跳出iframe页面问题
    登陆页面增加javascript*/
    if (top.location !== self.location) {
        top.location = self.location;
    }
</script>