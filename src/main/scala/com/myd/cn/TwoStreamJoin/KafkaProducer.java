package com.myd.cn.TwoStreamJoin;


import kafka.producer.KeyedMessage;
import kafka.producer.Producer;
import kafka.producer.ProducerConfig;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class KafkaProducer {



        //private  static Logger log = Logger.getLogger(KafkaProducerUtil.class);
        public static void sendMessgageFromLogFile(File logFile, String topic){
            BufferedInputStream buff = null;




            Properties props = new Properties();
            //props.put("metadata.broker.list", "hadoop26.geotmt.com:6667,hadoop206.geotmt.com:6667,hadoop207.geotmt.com:6667");
            props.put("metadata.broker.list", "xyy-hadoop-kafka-11.geotmt.com:6667,xyy-hadoop-kafka-12.geotmt.com:6667,xyy-hadoop-kafka-13.geotmt.com:6667");

            //配置value的序列化类
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            //配置key的序列化类
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");

            ProducerConfig producerConfig = new ProducerConfig(props);


            FileReader fr = null;
            Producer producer = new Producer(producerConfig);
            try {
                //InputStreamReader isr = new InputStreamReader(new FileInputStream(logFile));
                //BufferedReader br = new BufferedReader(isr);


                fr = new FileReader(logFile);
                BufferedReader br = new BufferedReader(fr);

                String line ;
                try {
                    //从字节数组byte[] 里面读取数据
                    while ((line = br.readLine()) != null){
                        //line = br.readLine() 这个方法是从文件读取每行数据
                        //log.info("============发送的数据============ "+line);
                        System.out.println("============发送的数据============ "+line);
                        //producer.send(new KeyedMessage<String, String>("dp-datacenter-damonitor-yzxyrh-sink-topic",line));
                        //producer.send(new KeyedMessage<String, String>("dp_monitor_rh_out-20190717",line));
                        //producer.send(new KeyedMessage<String,String>(topic,line));
                        //producer.send( new KeyedMessage<String,String>(topic,line));



                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (null != fr ){
                    try {
                        fr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public static void main(String[] args) {
            sendMessgageFromLogFile(new File(args[0]),args[1]);


        }

    }
