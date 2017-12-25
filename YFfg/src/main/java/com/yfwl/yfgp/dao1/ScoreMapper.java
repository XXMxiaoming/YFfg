package com.yfwl.yfgp.dao1;

import java.util.List;

import com.yfwl.yfgp.model.Score;

public interface ScoreMapper {

	List<Score> getScoreList(Score score);

	Integer initScore(Score score);

	Score getr7Score(Score score);

	Score getr30Score(Score score);

	Score getr6mScore(Score score);

	Score getr1yScore(Score score);
	
	//2016-08-05 获取七日收益的最近7七天数据
	List<Score> getR7dayScoreList(Score score);

	List<Score> getr7ScoreList(Score score);

	//2016-08-06 获取30日收益的最近30天数据
	List<Score> getR30dayScoreList(Score score);
	
	List<Score> getr30ScoreList(Score score);
	
	//2016-08-06 获取60日收益的最近60天数据
	List<Score> getR60dayScoreList(Score score);  

	List<Score> getr6mScoreList(Score score);
	
	//2016-08-06 获取1年收益的最近1年(360天)数据
	List<Score> getR1yearScoreList(Score score);  

	List<Score> getr1yScoreList(Score score);

	Score getLastScore(Score score);
	

}
