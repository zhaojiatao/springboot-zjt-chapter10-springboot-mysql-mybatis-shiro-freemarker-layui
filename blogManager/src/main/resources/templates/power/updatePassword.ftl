<@com.head title="修改密码">
    <base id="base" href="${basePath!}/">
    <link href="${basePath!}/static/layui/css/layui.css" type="text/css" media="screen" rel="stylesheet"/>
    <script src="${basePath!}/static/layui/layui.js" type="text/javascript"></script>

<script>
    //一般直接写在一个js文件中
    layui.use(['layer','form','table'], function(){
        var layer = layui.layer
            ,form = layui.form
            ,$ = layui.$
            ,laytpl = layui.laytpl
            ,table = layui.table;


        //监听表单提交事件
        form.on('submit(submitButton)', function (data) {
            $("#userId").val(${currentUser.id!});
            $.ajax({
                type: "POST",
                url:"admin/user/updatePassword",
                data:$("#formid").serialize(),
                async: false,
                error: function(request) {
                    layer.alert("与服务器连接失败/(ㄒoㄒ)/~~");
                },
                success: function(data) {
                    if(data.state=='fail'){
                        layer.alert(data.mesg);
                        return false;
                    }
                    if(data.state=='success'){
                        layer.open({
                            content: data.mesg,
                            yes: function(index, layero){
                                //do something
                                window.location.href="${basePath!}/user/logout";
                            }
                        });
                        return false;
                    }
                }
            });

            return false;//防止表单提交
        });



    });
</script>

</@com.head>
<@com.body>

<div class="layui-fluid">

    <form class="layui-form" action="" id="formid">
        <input type="hidden" name="id" id="userId" value=""/>
        <div class="layui-form-item">
            <label class="layui-form-label">用户名</label>
            <div class="layui-input-block">
                <input type="text" name="userName" readonly value="${currentUser.userName!}" lay-verify="required" placeholder="请输入用户名" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">旧密码</label>
            <div class="layui-input-inline">
                <input type="password" name="oldPassword" required lay-verify="required" placeholder="请输入旧密码" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">旧密码</div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">新密码</label>
            <div class="layui-input-inline">
                <input type="password" name="password" required lay-verify="required" placeholder="请输入新密码" autocomplete="off" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">新密码</div>
        </div>

        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit lay-filter="submitButton">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>




</div>




</@com.body>