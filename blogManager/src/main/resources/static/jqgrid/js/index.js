$(function(){
	//页面加载完成之后执行
	pageInit();
});
function pageInit(){
	//创建jqGrid组件
	jQuery("#list2").jqGrid(
			{
				url : 'static/jqgrid/data/JSONData.json',//组件创建完成之后请求数据的url
				datatype : "json",//请求数据返回的类型。可选json,xml,txt
				colNames : [ '编号', '用户名', '密码', '真实姓名', '备注','拥有角色', '角色设置' ],//jqGrid的列显示名字
				colModel : [ //jqGrid每一列的配置信息。包括名字，索引，宽度,对齐方式.....
				             {name : 'id',index : 'id',width : 100, sortable:true },
				             {name : 'invdate',index : 'invdate',width : 180},
				             {name : 'name',index : 'name asc, invdate',width : 200},
				             {name : 'amount',index : 'amount',width : 180,align : "right"},
				             {name : 'tax',index : 'tax',width : 180,align : "right"},
				             {name : 'total',index : 'total',width : 180,align : "right"},
				             {name : 'note',index : 'note',width : 300,sortable : false}
				           ],
				rowNum : 10,//一页显示多少条
				rowList : [ 10, 20, 30 ],//可供用户选择一页显示多少条
				pager : '#pager2',//表格页脚的占位符(一般是div)的id
				sortname : 'id',//初始化的时候排序的字段
				sortorder : "desc",//排序方式,可选desc,asc
				mtype : "get",//向后台请求数据的ajax的类型。可选post,get
				viewrecords : true,//定义是否要显示总记录数
				caption : "用户管理",//表格的标题名字
                height: "100%",
                autowidth:true,//如果为ture时，则当表格在首次被创建时会根据父元素比例重新调整表格宽度。如果父元素宽度改变，为了使表格宽度能够自动调整则需要实现函数：setGridWidth
                cellEdit:true//启用或者禁用单元格编辑功能
			});
	/*创建jqGrid的操作按钮容器*/
	/*可以控制界面上增删改查的按钮是否显示*/
	jQuery("#list2").jqGrid('navGrid', '#pager2', {edit : true,add : true,del : true});
}