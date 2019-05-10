package com.cennavi.audi_data_collect.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTimes {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");

        try {
            Connection connection2 = null;
            Class.forName("org.postgresql.Driver");
            String url2 = "jdbc:postgresql://117.51.149.90:8001/audi_data";
            String user2 = "postgres";
            String pass2 = "superman";
            connection2=  DriverManager.getConnection(url2,user2,pass2);
            Statement st = connection2.createStatement();

            String sql1 = "insert into trip_segment_new (id,car_id,time,segment_id,day) values (?,?,?,?,?)";
            PreparedStatement pstmt = connection2.prepareStatement(sql1);

            String sql = "select * from trip_segment WHERE  day=506 --ORDER BY segment_id,car_id,time";
            ResultSet list = st.executeQuery(sql);

            List<Map<String,Object>> resultList = new ArrayList<>();
            while (list.next()){
                Map<String,Object> resultMap = new HashMap<>();
                resultMap.put("id",list.getInt(1));
                resultMap.put("car_id",list.getString(2));
                resultMap.put("time",list.getString(3));
                resultMap.put("segment_id",list.getInt(4));
                resultMap.put("day",list.getInt(5));
                resultList.add(resultMap);
            }

            int segment1,segment2;
            int car_id1,car_id2;
            for(int i=0; i<resultList.size(); i++){

                if ((i + 1) < resultList.size()) {
                    segment1 = Integer.parseInt(resultList.get(i).get("segment_id").toString());
                    segment2 = Integer.parseInt(resultList.get(i + 1).get("segment_id").toString());
                    car_id1 = Integer.parseInt(resultList.get(i).get("car_id").toString());
                    car_id2 = Integer.parseInt(resultList.get(i + 1).get("car_id").toString());

                    if (segment1 == segment2) {        //判断是否在同一个路段内
                        if (car_id1 == car_id2) {       //是否是同一辆车
                            String time1 = resultList.get(i).get("time").toString().substring(11, 16);       //截取小时和分钟
                            //  if ((i + 1) < resultList.size()) {
                            String time2 = resultList.get(i + 1).get("time").toString().substring(11, 16);

                            int minute1 = Integer.parseInt(time1.substring(0, 2)) * 60 + Integer.parseInt(time1.substring(3));
                            int minute2 = Integer.parseInt(time2.substring(0, 2)) * 60 + Integer.parseInt(time2.substring(3));

                            if ((minute2 - minute1) < 60) {      //如果是连续的一个小时内
                                continue;
                            } else {
                                pstmt.setInt(1, Integer.parseInt(resultList.get(i).get("id").toString()));      //id
                                pstmt.setString(2, resultList.get(i).get("car_id").toString());      //car_id
                                pstmt.setString(3, resultList.get(i).get("time").toString());  //time
                                pstmt.setInt(4, Integer.parseInt(resultList.get(i ).get("segment_id").toString()));     //segment_id
                                pstmt.setInt(5, Integer.parseInt(resultList.get(i ).get("day").toString()));     //day
                                pstmt.addBatch();
                            }

                        } else {
                            pstmt.setInt(1, Integer.parseInt(resultList.get(i).get("id").toString()));      //id
                            pstmt.setString(2, resultList.get(i).get("car_id").toString());      //car_id
                            pstmt.setString(3, resultList.get(i).get("time").toString());  //time
                            pstmt.setInt(4, Integer.parseInt(resultList.get(i ).get("segment_id").toString()));     //segment_id
                            pstmt.setInt(5, Integer.parseInt(resultList.get(i ).get("day").toString()));     //day
                            pstmt.addBatch();
                        }
                    }else {
                        pstmt.setInt(1, Integer.parseInt(resultList.get(i ).get("id").toString()));      //id
                        pstmt.setString(2, resultList.get(i).get("car_id").toString());      //car_id
                        pstmt.setString(3, resultList.get(i ).get("time").toString());  //time
                        pstmt.setInt(4, Integer.parseInt(resultList.get(i).get("segment_id").toString()));     //segment_id
                        pstmt.setInt(5, Integer.parseInt(resultList.get(i ).get("day").toString()));     //day
                        pstmt.addBatch();
                    }
                }
            }

            int [] counts = pstmt.executeBatch();
            System.out.print(counts.length);

            pstmt.close();

            connection2.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
