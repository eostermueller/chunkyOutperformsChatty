package com.github.eostermueller.pgbench;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.SerializationUtil;
import com.github.eostermueller.pgbench.model.Transaction;

public class AccountsSerializationTest {

	@Test
	public void canSerializeSimpleAccount() throws ParserConfigurationException, TransformerException, SAXException, IOException {
		Account a = new Account();
		a.accountId = 101;
		a.balance = 102;
		a.branchId = 103;
		a.filler = "104";
	
		Accounts accounts = new Accounts();
		accounts.addAccount(a);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializationUtil as = new SerializationUtil();
		as.setOutputStream(baos);
		as.serialize( accounts );

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();
		assertEquals("Root tag name is wrong", SerializationUtil.TAG_NAME_ACCOUNTS, root.getNodeName() );
	
		NodeList nList = root.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT);
		assertEquals("didn't get the right number of  Account nodes",1,nList.getLength());
		 
		Node nNode = nList.item(0);
 
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
 
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			Element eElement = (Element) nNode;
			String temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT_ID).item(0).getTextContent();
			assertEquals("Couldn't find right accountId in serialized XML","101", temp);

			temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BALANCE).item(0).getTextContent();
			assertEquals("Couldn't find right accountId in serialized XML","102", temp);

			temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BRANCH_ID).item(0).getTextContent();
			assertEquals("Couldn't find right accountId in serialized XML","103", temp);

			temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_FILLER).item(0).getTextContent();
			assertEquals("Couldn't find right accountId in serialized XML","104", temp);
		}
	}
	@Test
	public void canSerializeMultipleAccounts() throws ParserConfigurationException, TransformerException, SAXException, IOException {
		Account a = new Account();
		a.accountId = 101;
		a.balance = 102;
		a.branchId = 103;
		a.filler = "104";
		Account b = new Account();
		b.accountId = 5101;
		b.balance = 5102;
		b.branchId = 5103;
		b.filler = "5104";
	
		Accounts accounts = new Accounts();
		accounts.addAccount(a);
		accounts.addAccount(b);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializationUtil as = new SerializationUtil();
		as.setOutputStream(baos);
		as.serialize( accounts );

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();
		assertEquals("Root tag name is wrong", SerializationUtil.TAG_NAME_ACCOUNTS, root.getNodeName() );
	
		NodeList nList = root.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT);
		assertEquals("didn't get the right number of  Account nodes",2,nList.getLength());
		
		for(int i = 0; i < 2 ; i++) {
			Node nNode = nList.item(i);
			 
			switch(i) {
				case 0:
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT_ID).item(0).getTextContent();
						assertEquals("Couldn't find right accountId in serialized XML","101", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BALANCE).item(0).getTextContent();
						assertEquals("Couldn't find right balance in serialized XML","102", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BRANCH_ID).item(0).getTextContent();
						assertEquals("Couldn't find right branchId in serialized XML","103", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_FILLER).item(0).getTextContent();
						assertEquals("Couldn't find right accountId in serialized XML","104", temp);
					}
					break;
				case 1:
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT_ID).item(0).getTextContent();
						assertEquals("Couldn't find right accountId in serialized XML","5101", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BALANCE).item(0).getTextContent();
						assertEquals("Couldn't find right Balance in serialized XML","5102", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_BRANCH_ID).item(0).getTextContent();
						assertEquals("Couldn't find right BranchId in serialized XML","5103", temp);
	
						temp = eElement.getElementsByTagName(SerializationUtil.TAG_NAME_FILLER).item(0).getTextContent();
						assertEquals("Couldn't find right Filler in serialized XML","5104", temp);
					}
					break;
			}
		}
	}
	@Test
	public void canSerializeSimpleTransactionList() throws ParserConfigurationException, TransformerException, SAXException, IOException {
		Account a = new Account();
		a.accountId = 101;
		a.balance = 102;
		a.branchId = 103;
		a.filler = "104";
		
		Transaction t = new Transaction();
		t.accountId = 5101;
		t.branchId = 5102;
		t.delta = 5103;
		t.mtime = new Date();
		t.filler = "5105";
		t.historyId = 5106;
		t.tellerId = 5107;
		a.transactions.add(t);
	
		Accounts accounts = new Accounts();
		accounts.addAccount(a);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SerializationUtil as = new SerializationUtil();
		as.setOutputStream(baos);
		as.serialize( accounts );

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(baos.toByteArray()));
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();
		assertEquals("Root tag name is wrong", SerializationUtil.TAG_NAME_ACCOUNTS, root.getNodeName() );
	
		NodeList nList = root.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT);
		assertEquals("didn't get the right number of  Account nodes",1,nList.getLength());
		 
		Node accountNode = nList.item(0);
		if (accountNode.getNodeType() == Node.ELEMENT_NODE) {
			Element accountEle = (Element) accountNode;

			String temp = accountEle.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT_ID).item(0).getTextContent();
			assertEquals("Couldn't find right accountId in serialized XML","101", temp);


			NodeList transactionList = accountEle.getElementsByTagName(SerializationUtil.TAG_NAME_TRANSACTION);
			assertEquals("didn't get the right number of  Transaction nodes",1,transactionList.getLength());
			Node transactionNode = transactionList.item(0);
			if (transactionNode.getNodeType() == Node.ELEMENT_NODE) {
				Element transactionElement = (Element) transactionNode;
				
				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_ACCOUNT_ID).item(0).getTextContent();
				assertEquals("Couldn't find right accountId in serialized XML","5101", temp);

				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_BRANCH_ID).item(0).getTextContent();
				assertEquals("Couldn't find right BranchId in serialized XML","5102", temp);

				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_DELTA).item(0).getTextContent();
				assertEquals("Couldn't find right Balance in serialized XML","5103", temp);
				
				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_TIME).item(0).getTextContent();
				//Sat Oct 04 22:58:26 CDT 2014
				assertNotNull("Couldn't find right MTime in serialized XML",temp);

				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_FILLER).item(0).getTextContent();
				assertEquals("Couldn't find right Filler in serialized XML","5105", temp);
				
				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_HISTORY_ID).item(0).getTextContent();
				assertEquals("Couldn't find right HistoryId in serialized XML","5106", temp);

				temp = transactionElement.getElementsByTagName(SerializationUtil.TAG_NAME_TELLER_ID).item(0).getTextContent();
				assertEquals("Couldn't find right TellerId in serialized XML","5107", temp);

			}
		}
	}
}
