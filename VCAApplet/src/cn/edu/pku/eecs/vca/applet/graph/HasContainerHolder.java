package cn.edu.pku.eecs.vca.applet.graph;

import cn.edu.pku.eecs.vca.core.Node;

public interface HasContainerHolder												//画依存树时有包含面板的组件
{
	public ContainerHolder getContainerHolder();
	public Node getNode();
}
