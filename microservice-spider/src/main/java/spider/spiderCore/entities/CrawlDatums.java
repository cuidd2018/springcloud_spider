package spider.spiderCore.entities;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 用于存储多个CrawlDatum的数据结构
 *
 * @author hu
 */
public class CrawlDatums implements Iterable<CrawlDatum> {

    protected LinkedList<CrawlDatum> dataList = new LinkedList<CrawlDatum>();

    public CrawlDatums() {
    }

    
    public CrawlDatums(Iterable<String> links) {
        add(links);
    }

    public CrawlDatums(CrawlDatums datums) {
        add(datums);
    }

    public CrawlDatums(Collection<CrawlDatum> datums) {
        for (CrawlDatum datum : datums) {
            this.add(datum);
        }
    }

    public CrawlDatums add(CrawlDatum datum) {
        dataList.add(datum);
        return this;
    }

    public CrawlDatums add(String url) {
        CrawlDatum datum = new CrawlDatum(url);
        return add(datum);
    }

    public CrawlDatums add(CrawlDatums datums) {
        dataList.addAll(datums.dataList);
        return this;
    }

    public CrawlDatums add(Iterable<String> links) {
        for (String link : links) {
            add(link);
        }
        return this;
    }

    public CrawlDatum addAndReturn(String url){
        CrawlDatum datum = new CrawlDatum(url);
        add(datum);
        return datum;
    }

    public CrawlDatums addAndReturn(Iterable<String> links){
        CrawlDatums datums = new CrawlDatums(links);
        add(datums);
        return datums;
    }

    public CrawlDatums addAndReturn(CrawlDatums datums){
        add(datums);
        return datums;
    }

    @Override
    public Iterator<CrawlDatum> iterator() {
        return dataList.iterator();
    }

    public CrawlDatum get(int index) {
        return dataList.get(index);
    }

    public int size() {
        return dataList.size();
    }

    public CrawlDatum remove(int index) {
        return dataList.remove(index);
    }

    public boolean remove(CrawlDatum datum) {
        return dataList.remove(datum);
    }

    public void clear() {
        dataList.clear();
    }

    public boolean isEmpty() {

        return dataList.isEmpty();
    }

    public int indexOf(CrawlDatum datum) {
        return dataList.indexOf(datum);
    }

    @Override
    public String toString() {
        return dataList.toString();
    }

}
