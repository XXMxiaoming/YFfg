package com.yfwl.yfgp.dao1;

import java.util.List;
import com.yfwl.yfgp.model.Optitask;

public interface OptitaskMapper {
	List<Optitask> getAllRunOptitask();
	List<Optitask> getOptitask(Optitask opti);
	Optitask getOptibuytask(Optitask opti);
	Optitask getOptiselltask(Optitask opti);
	Integer insertOptitask(Optitask opti);
	Integer deleteOptitask(Optitask opti);
	Integer updateOptitask(Optitask opti);
	
}
