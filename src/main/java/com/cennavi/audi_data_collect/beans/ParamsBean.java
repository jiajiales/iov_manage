package com.cennavi.audi_data_collect.bean;

import org.springframework.stereotype.Component;

/**
 * Created by cennavi on 2019/5/7.
 */
@Component
public class ParamsBean {
    private String city;
    private String dataList[];
    private String dataListFormat[];
    private String eventsList[];
    private String eventType;
    private String isContinuous;
    private Integer roadSecList[];
    private String sort;
    private String timeFrame[];
    private Integer segmentId[];
    private Integer offset; 
    private Integer limit;
    private String eventId;

     

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer[] getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Integer[] segmentId) {
        this.segmentId = segmentId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String[] getDataList() {
        return dataList;
    }

    public void setDataList(String[] dataList) {
        this.dataList = dataList;
    }

    public String[] getDataListFormat() {
        return dataListFormat;
    }

    public void setDataListFormat(String[] dataListFormat) {
        this.dataListFormat = dataListFormat;
    }

    public String[] getEventsList() {
        return eventsList;
    }

    public void setEventsList(String[] eventsList) {
        this.eventsList = eventsList;
    }

    public String getIsContinuous() {
        return isContinuous;
    }

    public void setIsContinuous(String isContinuous) {
        this.isContinuous = isContinuous;
    }

    public Integer[] getRoadSecList() {
        return roadSecList;
    }

    public void setRoadSecList(Integer[] roadSecList) {
        this.roadSecList = roadSecList;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String[] getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String[] timeFrame) {
        this.timeFrame = timeFrame;
    }
}
