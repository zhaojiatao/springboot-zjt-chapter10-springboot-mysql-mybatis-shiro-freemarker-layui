<@com.head title="">
<base id="base" href="${basePath!}/">
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<link href="${basePath!}/static/layui/css/layui.css" type="text/css" media="screen" rel="stylesheet"/>
<link href="${basePath!}/static/css/ztree/metroStyle/metroStyle.css" type="text/css" media="screen" rel="stylesheet"/>
<link href="${basePath!}/static/css/ztree/demo.css" type="text/css" media="screen" rel="stylesheet"/>
<script src="${basePath!}/static/js/jquery.min.js" type="text/javascript"></script>
<script src="${basePath!}/static/layui/layui.js" type="text/javascript"></script>
<script src="${basePath!}/static/js/ztree/jquery.ztree.all.js" type="text/javascript"></script>
<script>
    var zTreeObj1;
    var layerid;
    //一般直接写在一个js文件中
    layui.use(['layer', 'form','table'], function(){
        var layer = layui.layer
                ,form = layui.form
                ,$ = layui.$
                ,laytpl = layui.laytpl
                ,table = layui.table;

        pageInit();

        $("#choseIcon").click(function(){
            layer.open({
                type: 2,
                area:['95%', '95%'],
                content:'${basePath!}/static/page/systemSetting/icons.html',
                shadeClose:true,
                end: function(){
                    $("#icon").select();
                }
            });
            return false;
        });


    });


    function pageInit() {
        // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
        var setting = {
            view: {
                addHoverDom: addHoverDom,//添加鼠标悬浮事件
                removeHoverDom: removeHoverDom,//鼠标悬浮接触事件
                selectedMulti: false
            },
            edit: {
                enable: true,//允许编辑
                editNameSelectAll: true,
                showRemoveBtn: showRemoveBtn//显示删除按钮
            },
            callback: {
                beforeEditName: beforeEditName,
                beforeRemove: beforeRemove,//删除前触发事件
                onRemove: onRemove//删除触发
            }
        };

        var log, className = "dark";

        function beforeEditName(treeId, treeNode) {
            className = (className === "dark" ? "":"dark");
            showLog("[ "+getTime()+" beforeEditName ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.selectNode(treeNode);
            setTimeout(function() {
                if (confirm("进入节点 -- " + treeNode.name + " 的编辑状态吗？")) {

                    //根据id获取菜单节点对象的数据并填充进编辑的表单中
                    $.ajax({
                        type: "POST",
                        url:"admin/menu/selectMenuById",
                        data:{id:treeNode.id},
                        async: false,
                        error: function(request) {
                            layer.alert("与服务器连接失败/(ㄒoㄒ)/~~");
                        },
                        success: function(data) {
                            if(data.state=='fail'){
                                layer.open({
                                    skin: 'layui-layer-molv',
                                    type:1,
                                    area:"20%",
                                    content:data.mesg,
                                    shadeClose:true,
                                    end: function(){
                                        layer.close(layerid);
                                        window.location.reload();//刷新框架
                                    }
                                });
                                return false;
                            }
                            if(data.state=='success'){
                                $("#editlabelid").html(treeNode.id);//设置父节点id，表单中仅需要提供父级节点的id即可，后台会根据pId查询
                                $("#icon").val(data.tmenu.icon);
                                $("#name").val(data.tmenu.name);
                                $("#url").val(data.tmenu.url);
                                layerid=layer.open({//开启表单弹层
                                    skin: 'layui-layer-molv',
                                    area:'60%',
                                    type: 1,
                                    title:'新建菜单节点',
                                    content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                                });

                            }
                        }
                    });


                }
            }, 0);
            return false;
        }

        //哪些节点会显示删除按钮，哪些节点不会显示
        function showRemoveBtn(treeId, treeNode) {
            return treeNode.name!='systemeSttings'
                    &&treeNode.name!='菜单管理'
                    &&treeNode.name!='角色管理'
                    &&treeNode.name!='用户管理'
                    && treeNode.pId !='-1';
        }

        //删除之前提示确认
        function beforeRemove(treeId, treeNode) {
            className = (className === "dark" ? "":"dark");
            showLog("[ "+getTime()+" beforeRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.selectNode(treeNode);
            return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
        }
        //删除时触发
        function onRemove(e, treeId, treeNode) {
            showLog("[ "+getTime()+" onRemove ]&nbsp;&nbsp;&nbsp;&nbsp; " + treeNode.name);

            $.ajax({
                type: "POST",
                url:"admin/menu/deletemenu",
                data:{id:treeNode.id},
                async: false,
                error: function(request) {
                    layer.alert("与服务器连接失败/(ㄒoㄒ)/~~");
                },
                success: function(data) {
                    if(data.state=='fail'){
                        layer.open({
                            skin: 'layui-layer-molv',
                            type:1,
                            area:"20%",
                            content:data.mesg,
                            shadeClose:true,
                            end: function(){
                                layer.close(layerid);
                                window.location.reload();//刷新框架
                            }
                        });
                        return false;
                    }
                    if(data.state=='success'){
                        layer.open({
                            skin: 'layui-layer-molv',
                            type:1,
                            area:"20%",
                            content:data.mesg,
                            shadeClose:true,
                            end: function(){
                                layer.close(layerid);
                                window.parent.location.reload();//刷新框架
                            }
                        });

                    }
                }
            });



        }
        function showLog(str) {
            if (!log) log = $("#log");
            log.append("<li class='"+className+"'>"+str+"</li>");
            if(log.children("li").length > 8) {
                log.get(0).removeChild(log.children("li")[0]);
            }
        }
        function getTime() {
            var now= new Date(),
                    h=now.getHours(),
                    m=now.getMinutes(),
                    s=now.getSeconds(),
                    ms=now.getMilliseconds();
            return (h+":"+m+":"+s+ " " +ms);
        }


        //鼠标悬浮时临时添加按钮
        function addHoverDom(treeId, treeNode) {
            //3级目录不可再增加子菜单
            if(treeNode.state=='3'||treeNode.name=='菜单管理'){
                return false;
            }else{

                var sObj = $("#" + treeNode.tId + "_span");
                if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
                var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
                        + "' title='add node' onfocus='this.blur();'></span>";
                sObj.after(addStr);
                var btn = $("#addBtn_"+treeNode.tId);
                if (btn) btn.bind("click", function(){//点击加号时触发
                    var zTree = $.fn.zTree.getZTreeObj("treeDemo");

                    //初始化新增表单
                    $("#editlabelpId").html(treeNode.id);//设置父节点id，表单中仅需要提供父级节点的id即可，后台会根据pId查询
                    $("#reset").click();//重置表单(新建时在进入表单前要重置一下表单的内容，不然表单打开后会显示上一次的表单的内容。这里调用表单中重置按钮的点击方法来重置)
                    layerid=layer.open({//开启表单弹层
                        skin: 'layui-layer-molv',
                        area:'60%',
                        type: 1,
                        title:'新建菜单节点',
                        content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                    });
                    //zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.id, name:"new node" + (newCount++)});
                    return false;
                });

            }
        };



        //监听提交(新增和编辑)
        layui.form.on('submit(addeditsubmitfilter)', function(data) {

            //为了防止form中的id值被点击重置后置空,将编辑的id存放在label中，在表单提交时从label中提取出值，赋给表单input
            $("#pId").val($("#editlabelpId").html());
            $("#editid").val($("#editlabelid").html());

            $.ajax({
                type: "POST",
                url:"admin/menu/addupdatemenu",
                data:$('#addeditformid').serialize(),
                async: false,
                error: function(request) {
                    layer.alert("与服务器连接失败/(ㄒoㄒ)/~~");
                },
                success: function(data) {
                    if(data.state=='fail'){
                        layer.open({
                            skin: 'layui-layer-molv',
                            type:1,
                            area:"20%",
                            content:data.mesg,
                            shadeClose:true,
                            end: function(){
                                layer.close(layerid);
                                window.location.reload();//刷新框架
                            }
                        });
                    }
                    if(data.state=='success'){
                        layer.open({
                            skin: 'layui-layer-molv',
                            type:1,
                            area:"20%",
                            content:data.mesg,
                            shadeClose:true,
                            end: function(){
                                layer.close(layerid);
                                window.parent.location.reload();//刷新框架
                            }
                        });

                    }
                }
            });



            return false;
        });


        //移除鼠标悬浮时临时增加的按钮
        function removeHoverDom(treeId, treeNode) {
            $("#addBtn_"+treeNode.tId).unbind().remove();
        };

        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
        var zNodes = [
            {name:"test1", open:true, children:[
                {name:"test1_1"}, {name:"test1_2"}]},
            {name:"test2", open:true, children:[
                {name:"test2_1"}, {name:"test2_2"}]}
        ];


        //初始化ztree(注意是-1)
        $.ajax({
            type: "POST",
            url:'admin/menu/loadCheckMenuInfo?parentId=-1',
            async: false,
            dataType: 'json',
            timeout: 1000,
            cache: false,
            error: function(request) {
                layer.alert("与服务器连接失败/(ㄒoㄒ)/~~");
            },
            success: function(data) {
                zNodes=data;
                zTreeObj1 = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            }
        });


    }


</script>

</@com.head>
<@com.body>

<div  id="setpermisdiv" class="layui-fluid" >
    <ul id="treeDemo" class="ztree"></ul>
</div>






<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden/>
        <label hidden="true" id="editlabelpId"></label>
        <input id="pId" name="pId" value="" hidden/>
        <label hidden="true" id="tId"></label>
        <div class="layui-form-item">
            <label class="layui-form-label">图标</label>
            <div class="layui-input-block">
                <button class="layui-btn" id="choseIcon">选择图标</button>
                <input type="text" id="icon" name="icon" autocomplete="off" placeholder="请输入图标" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">菜单名称</label>
            <div class="layui-input-block">
                <input type="text" id="name" name="name" autocomplete="off" placeholder="请输入菜单名称" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">url</label>
            <div class="layui-input-block">
                <input type="text" id="url" name="url" autocomplete="off" placeholder="请输入url" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="addeditsubmitfilter">立即提交</button>
                <button id="reset" type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>

    </form>
</div>




</@com.body>