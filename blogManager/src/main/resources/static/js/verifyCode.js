//点击换一张验证码
function changeImg() {
	var imgSrc = $("#imgObj");   
    var src = imgSrc.attr("src");   
    imgSrc.attr("src",chgUrl(src));  
    $("#info").html("");
}
//时间戳   
//为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳   
function chgUrl(url) {
	var timestamp = (new Date()).valueOf();
	var dex=url.indexOf("drawImage")+"drawImage".length;
    url = url.substring(0, dex);
	if ((url.indexOf("&") >= 0)) {
		url = url + "×tamp=" + timestamp;   
	} else {
		url = url + "?timestamp=" + timestamp;   
	}
	return url;
}
//验证码验证
function isRightCode() {
	var code = $("#veryCode").attr("value");
	//alert(code);
	code = "c=" + code;
	$.ajax( {
		type : "POST",
		url : "ResultServlet",
		data : code,
		success : callback
	});
}
//验证以后处理提交信息或错误信息
function callback(data) {
	if(data.toString()==1)
	{
		$("#info").html("xw素材网提醒您：成功了！");
	  return;
	}else
	{
		$("#info").html(data);
		return;
	}
}  
