package com.hadoop.test.l3;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;

public class WordCountOldAPI {

    public static class MyMapper extends MapReduceBase
            implements Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key,
                        Text value,
                        OutputCollector<Text, IntWritable> output,
                        Reporter reporter) throws IOException {
            output.collect(new Text(value.toString()), new IntWritable(1));
        }
    }

    public static class MyReducer extends MapReduceBase
            implements Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key,
                           Iterator<IntWritable> values,
                           OutputCollector<Text, IntWritable> output,
                           Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(WordCountOldAPI.class);
        /*
        JobConf:
        Hadoop框架中配置MapReduce任务的主要接口
        框架会按照JobConf对象中的配置信息来执行作业
         */

        conf.setJobName("wordcount");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        /*
        conf.setOutputKeyClass、conf.setOutputValueClass:
        指定了输出的键（Key）类和值（Value）类，应与Reducer类相匹配
        否则作业执行时会抛出RuntimeException异常
         */

        conf.setMapperClass(MyMapper.class);
        conf.setCombinerClass(MyReducer.class);
        conf.setReducerClass(MyReducer.class);
        conf.setNumReduceTasks(1);

        conf.setInputFormat(TextInputFormat.class);
        /*
        TextInputFormat:
        向框架声明其输入文件为文本格式，是InputFormat的子类
        TextInputFormat类会读取输入文件中的每行作为一个记录
         */

        conf.setOutputFormat(TextOutputFormat.class);
        /*
        TextOutputFormat:
        指定了MapReduce作业的输出情况
        如，它会检测其输出目录是否存在，若存在，Hadoop系统会拒绝该作业的执行
        TextOutputFormat类用来声明其MapReduce作业的输出格式为文本格式文件
         */

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        /*
        FileInputFormat.setInputPaths(conf, new Path(args[0])):
        向Hadoop框架声明了要读取的文件所在的目录
        可包含一个或多个文件，每个文件中每行含有一个单词
        注意setInputPaths中使用的是复数，该方法可以给Hadoop配置一个文件目录数组做为输入数据路径
        这些输入目录中的所有文件构成了该作业的输入数据源
         */

        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        /*
        FileOutputFormat.setOutputPath(conf, new Path(args[1])):
        指定了作业的输出目录，作业最终的结果输出会保存到该目录下
         */

        /*
        Reducer实例的执行数量默认为1，可改变，常用来提高程序的运行效率
        调用JobConf.class实例中的setNumReduceTasks(int n)可配置该参数
         */

        JobClient.runJob(conf);
    }
}
