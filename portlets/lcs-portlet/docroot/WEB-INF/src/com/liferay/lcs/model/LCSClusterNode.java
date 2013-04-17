package com.liferay.lcs.model;

/**
 * @author Ivica Cardic
 */
public class LCSClusterNode {

	public long getLcsClusterEntryId() {
		return _lcsClusterEntryId;
	}

	public long getLcsClusterNodeId() {
		return _lcsClusterNodeId;
	}

	public void setLcsClusterEntryId(long lcsClusterEntryId) {
		_lcsClusterEntryId = lcsClusterEntryId;
	}

	public void setLcsClusterNodeId(long lcsClusterNodeId) {
		_lcsClusterNodeId = lcsClusterNodeId;
	}

	private long _lcsClusterEntryId;
	private long _lcsClusterNodeId;
}