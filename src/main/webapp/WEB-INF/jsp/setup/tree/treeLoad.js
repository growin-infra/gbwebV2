function tClick(event, treeId, treeNode, clickFlag) {
	console.log("node name["+treeNode.name+"]\nid["+treeNode.id+"]\npid["+treeNode.pId+"]");
}

function fn_loadset() {
	var comAjax = new ComAjax();
	comAjax.setUrl("/findTree");
	comAjax.setCallback("fn_treeload");
	comAjax.ajax();
}

function fn_treeload(data) {
	var list = data.list;
	if (list != null) {
		var str = "[";
		for (var i=0; i<list.length; i++) {
			str += "{";
			str += "'id':"+list[i].bmt_id+",'pId':"+list[i].bmt_pid+",'name':'"+list[i].bmt_nam+"'";
			if (i == 0) str += ",'open':'true','isParent':'true'";
			if (i == 1) str += ",'isParent':'true'";
			str += "}";
			if ((i+1 != list.length)) str += ",";
		}
		str += "]";
	} else {
		str = "[{'id':1,'pId':0,'name':'백업관리서버','open':'true','isParent':'true'}]";
	}
	$.fn.zTree.init($("#treeDemo"), tSetting, eval(str));
}