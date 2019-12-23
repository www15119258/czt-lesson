package com.cangzhitao.lesson.news_smart_classified;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class CosinCalc {

	private static final Logger logger = LoggerFactory.getLogger(CosinCalc.class);

	public static void main(String[] args) {
		System.out.println(calcNews("new.txt", "new1.txt"));
		System.out.println(calcNews("new.txt", "new2.txt"));
		System.out.println(calcNews("new.txt", "new3.txt"));
		System.out.println(calcNews("new.txt", "new4.txt"));
		System.out.println(calcNews("new.txt", "new5.txt"));
		System.out.println(calcNews("new.txt", "new6.txt"));
	}
	
	public static Double calcNews(String new1, String new2) {
		String text1 = readFromFile(CosinCalc.class.getClassLoader().getResource(new1).getPath());
		Map<String, Integer> map1 = calcFrequency(text1);
		
		String text2 = readFromFile(CosinCalc.class.getClassLoader().getResource(new2).getPath());
		Map<String, Integer> map2 = calcFrequency(text2);
		return calcCosin(map1, map2);
	}

	/**
	 * 读取文本
	 * @param path
	 * @return
	 */
	public static String readFromFile(String path) {
		File file = new File(path);
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append(System.lineSeparator());
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static Map<String, Integer> calcFrequency(String text) {
	    try {
	    	Map<String, Integer> map = new HashMap<>();
	    	if (text == null || "".equals(text.trim())) {
	    		return map;
	    	}
	    	Configuration con = DefaultConfig.getInstance();
	    	con.setUseSmart(true);
	    	IKSegmenter ik = new IKSegmenter(new StringReader(text), con);
	    	List<String> list = new ArrayList<>();
	        Lexeme word = null;
	        while((word=ik.next())!=null) {      
	        	list.add(word.getLexemeText());
	        }
	        list.stream().forEach(temp -> map.put(temp, map.getOrDefault(temp, 0) + 1));
	        return map;
	    } catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new HashMap<>();
		}
	}
	
	/**
	 * 计算余弦值
	 * @param map1
	 * @param map2
	 * @return
	 */
	public static double calcCosin(Map<String, Integer> map1, Map<String, Integer> map2) {
		//如果两者都为空，则认为两者完全一致
		if (map1.isEmpty() && map2.isEmpty()) {
			return 0d;
		//如果两者有一者为空，则认为两者完全不一致
		} else if (map1.isEmpty() || map2.isEmpty()) {
			return 1d;
		}
		// 存储词汇量并集
		Set<String> set = new HashSet<>();
		set.addAll(map1.keySet());
		set.addAll(map2.keySet());
		
		Iterator<String> it = set.iterator();
		
		//获取两者特征量
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		while(it.hasNext()) {
			String word = it.next();
			if (map1.get(word) != null) {
				list1.add(map1.get(word));
			} else {
				list1.add(0);
			}
			if (map2.get(word) != null) {
				list2.add(map2.get(word));
			} else {
				list2.add(0);
			}
		}
		//向量积
		Double ab = 0d;
		//a,b向量长度
		Double a = 0d;
		Double b =  0d;
		for (int i = 0; i < list1.size(); i++) {
			ab +=  list1.get(i) * list2.get(i);
			a += list1.get(i) * list1.get(i);
			b += list2.get(i) * list2.get(i);
		}
		a = Math.sqrt(a);
		b = Math.sqrt(b);
		return BigDecimal.valueOf(ab).divide(BigDecimal.valueOf(a * b), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
