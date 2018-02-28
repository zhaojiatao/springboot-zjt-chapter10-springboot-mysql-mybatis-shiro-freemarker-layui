<#macro head  title="">
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="icon" href="/static/logo.png">

    <title>${title}</title>
    <#nested />
</head>
</#macro>

<#macro body>
<body>
<#--事实上IE8和IE9并不支持媒体查询（Media Queries），但你可以使用下面的补丁完美兼容！该补丁来自于开源社区：-->
<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
<script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
<script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<#--将上述代码放入你页面 <body> 标签内的任意位置-->
<#nested />
</body>
</html>
</#macro>