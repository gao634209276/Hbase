HBase导入数据的介绍 — HBase导入方式
使用HBase的Put  API
使用HBase的bulk load工具
自定义MapReduce任务
使用开源工具sqoop

HBase导入数据的介绍 — 导入方式之Put API
	Put导入是最简单的方式，但是却不是最有效的方式
	不适合快速的导入大量数据
	因为HBase在设计之初考虑的就是大容量数据的存储，所以Put API的导入方式并不是最有效的数据导入方式

HBase导入数据的介绍 — 导入方式之bulk load
	大量数据导入最为有效的方式
	因为bulk load通过MapReduce任务直接生成一个HBaes的HFile，然后再将数据导入到集群中
	使用bulk load最简单的方式是使用importtsv
	importtsv是一个内置的工具
	importtsv以MapReduce任务的方式，可以将TSV文件中的文本数据直接的导入到HBase的表中，或者导入到HBase已经格式化的文件中

HBase导入数据的介绍 — 导入方式之MapReduce
	如果要从其他格式或者数据是动态产生，使用MapReduce是最有效的数据导入方式
	虽然使用MapReduce是比较灵活的方式，但是因为MapReduce作业的负载，在数据量大的情况下，如果MapReduce作业设计不当，也可能会造成性能的低下

sqoop简介


























