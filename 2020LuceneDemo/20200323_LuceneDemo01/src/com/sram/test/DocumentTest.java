package com.sram.test;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;

public class DocumentTest {
    private IndexWriter indexWriter;
    @Before
    public void init() throws IOException {
        indexWriter = new IndexWriter(FSDirectory.open(new File("D:\\SJT1909\\index").toPath()),new IndexWriterConfig(new IKAnalyzer()));
    }

    @Test
    public void addDocument() throws IOException {
        Document document = new Document();

        document.add(new TextField("fileName","笑傲江湖", Field.Store.YES));
        document.add(new TextField("fileContent","金庸武侠小说代表作之一", Field.Store.YES));
        document.add(new StoredField("filePath","D:\\aaa"));

        indexWriter.addDocument(document);
        indexWriter.close();
    }

    @Test
    public void deleteDocument() throws IOException {
        indexWriter.deleteAll();
        //indexWriter.deleteDocuments(new Term("fileName","江湖"));
        indexWriter.close();
    }

    @Test
    public void updateDocument() throws IOException {
        Document document = new Document();

        document.add(new TextField("fileName","笑傲江湖", Field.Store.YES));

        indexWriter.updateDocument(new Term("fileName","web"),document);

        indexWriter.close();
    }
}
