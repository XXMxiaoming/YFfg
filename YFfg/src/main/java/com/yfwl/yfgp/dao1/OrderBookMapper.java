package com.yfwl.yfgp.dao1;

import java.util.List;

import com.yfwl.yfgp.model.OrderBook;

public interface OrderBookMapper {

	Integer deleteOrderBook(OrderBook orderbook);

	Integer initOrderBook(OrderBook orderbook);

	List<OrderBook> getOrderBookList(OrderBook orderbook);
	
	List<OrderBook> getOrderBookByGidAndStockid(OrderBook orderbook);

	OrderBook getOrderBook(OrderBook orderbook);

	List<OrderBook> getAllOrderBookList();

	Integer deleteAccountsOrder(OrderBook orderbook);
	
	
	
	List<OrderBook> getOrderBookList2(OrderBook orderbook);
}
