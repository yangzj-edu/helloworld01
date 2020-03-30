package com.sram.test;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class LuceneTest {
    @Test
    public void createIndex() throws IOException {
        //指定索引库保存的位置   ,创建Directory对象
        Directory directory = FSDirectory.open(new File("D:\\SJT1909\\index").toPath());

        //基于directory创建IndexWriter
        IKAnalyzer ikAnalyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(ikAnalyzer);//代表使用默认的标准分词器StandardAnalyzer
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);

        //读取源文件 每一个源文件都需要创建一个document对象
        File dir = new File("D:\\SJT1909\\Tools\\Lucene\\02.资料\\searchsource");
        File[] files = dir.listFiles();

        for (File file : files) {
            //System.out.println(file);
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            long fileSize = FileUtils.sizeOf(file);

            //创建域对象    域名称   域内容   是否存储
            Field fileName1 = new TextField("fileName", fileName, Field.Store.YES);
            //Field filePath1 = new TextField("filePath", filePath, Field.Store.YES);
            Field filePath1 = new StoredField("filePath",filePath);  //不分词  不索引  存储

            Field fileContent1 = new TextField("fileContent", fileContent, Field.Store.YES);
            //Field fileSize1 = new TextField("fileSize", fileSize+"", Field.Store.YES);
            Field fileSize1 = new LongPoint("fileSize", fileSize);//分词  索引  不存储
            Field fileSize2 = new StoredField("fileSize", fileSize);

            //创建document
            Document document = new Document();
            document.add(fileName1);
            document.add(filePath1);
            document.add(fileContent1);
            document.add(fileSize1);
            document.add(fileSize2);

            //将文档写入索引库
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws IOException {
        //1.指定索引库的位置
        Directory directory = FSDirectory.open(new File("D:\\SJT1909\\index").toPath());
        //2.创建IndexReader，读取索引库
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.创建IndexSearcher对象用来加载IndexReader
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //4.封装Query
        Query query = new TermQuery(new Term("fileName","web"));
        //5.执行查询   参数1：查询对象   参数2：显示条数
        TopDocs topDocs = indexSearcher.search(query, 5);
        //获取topDocs的个数
        System.out.println("一共有"+topDocs.totalHits+"条记录");
        //如果条数很多，需要用循环方式逐一获取

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println("文档名称：");
            System.out.println(document.get("fileName"));
            System.out.println("文档路径：");
            System.out.println(document.get("filePath"));
            System.out.println("文档内容：");
            System.out.println(document.get("fileContent"));
            System.out.println("================================================");
        }
        indexReader.close();
    }

    /**
     * 查看分词效果
     */
    @Test
    public void AnalyzerTest() throws IOException {
        //创建分词器对象   StandardAnalyzer 标准分词器
        Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new IKAnalyzer();
        //查看分词效果
        //TokenStream tokenStream = analyzer.tokenStream("", "疫情恐慌公安局思软智学教育奥利给全文数据库是全文检索系统的主要构成部分。所谓全文数据库是将一个完整的信息源的全部内容转化为计算机可以识别、处理的信息单元而形成的数据集合。");
        TokenStream tokenStream = analyzer.tokenStream("","13988888888");
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();

        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.close();
    }


    /**
     * 用来打印查询结果的方法
     * @throws IOException
     */
    public void print(Query query) throws IOException {
        //1.指定索引库的位置
        Directory directory = FSDirectory.open(new File("D:\\SJT1909\\index").toPath());
        //2.创建IndexReader，读取索引库
        IndexReader indexReader = DirectoryReader.open(directory);
        //3.创建IndexSearcher对象用来加载IndexReader
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        TopDocs topDocs = indexSearcher.search(query, 100);
        //获取topDocs的个数
        System.out.println("一共有"+topDocs.totalHits+"条记录");
        //如果条数很多，需要用循环方式逐一获取

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println("文档名称：");
            System.out.println(document.get("fileName"));
            System.out.println("文档路径：");
            System.out.println(document.get("filePath"));
            System.out.println("文档内容：");
            System.out.println(document.get("fileContent"));
            System.out.println("文档大小：");
            System.out.println(document.get("fileSize"));
            System.out.println("================================================");
        }
        indexReader.close();
    }


    @Test
    public void queryTest1() throws IOException {
        Query query = LongPoint.newRangeQuery("fileSize", 0L, 1000L);
        print(query);
    }

    @Test
    public void queryTest2() throws IOException, ParseException {
        QueryParser queryParser = new QueryParser("fileName",new IKAnalyzer());
        Query query = queryParser.parse("全文检索工具配合web页面开发项目");
        print(query);
    }




}
