package org.web3scala.model

import org.json4s.JsonAST.JValue
import scala.collection.immutable.HashMap
import scala.concurrent.Future

case class Request(jsonrpc: String = "2.0", method: String, var params: List[String] = List.empty[String], id: Int = 1)

trait Response
case class GenericResponse(jsonrpc: String, id: Int, error: Option[ErrorContent], result: Any) extends Response
case class Web3ClientVersion(jsonrpc: String, id: Int, result: String) extends Response
case class Web3Sha3(jsonrpc: String, id: Int, result: String) extends Response
case class NetVersion(jsonrpc: String, id: Int, result: Int) extends Response
case class NetListening(jsonrpc: String, id: Int, result: Boolean) extends Response
case class NetPeerCount(jsonrpc: String, id: Int, result: Int) extends Response
case class EthProtocolVersion(jsonrpc: String, id: Int, result: Int) extends Response
case class EthSyncingFalse(jsonrpc: String, id: Int, result: Boolean) extends Response
case class EthSyncingTrue(jsonrpc: String, id: Int, result: HashMap[_,_]) extends Response
case class EthCoinbase(jsonrpc: String, id: Int, result: String) extends Response
case class EthMining(jsonrpc: String, id: Int, result: Boolean) extends Response
case class EthHashrate(jsonrpc: String, id: Int, result: Long) extends Response
case class EthGasPrice(jsonrpc: String, id: Int, result: Long) extends Response
case class EthAccounts(jsonrpc: String, id: Int, result: List[_]) extends Response
case class EthBlockNumber(jsonrpc: String, id: Int, result: Long) extends Response
case class EthBalance(jsonrpc: String, id: Int, result: Long) extends Response
case class EthStorage(jsonrpc: String, id: Int, result: String) extends Response
case class EthTransactionCount(jsonrpc: String, id: Int, result: Long) extends Response
case class EthBlockTransactionCount(jsonrpc: String, id: Int, result: Long) extends Response
case class EthUncleCount(jsonrpc: String, id: Int, result: Long) extends Response
case class EthCode(jsonrpc: String, id: Int, result: String) extends Response
case class EthSign(jsonrpc: String, id: Int, result: String) extends Response


case class AsyncResponse(future: Future[JValue]) extends Response

case class Error(jsonrpc: String, id: Int, error: ErrorContent) extends Response
case class ErrorContent(code: Int, message: String) {
  override def toString: String = s"Error/code=$code/message=$message]"
}

trait Block
case class BlockName(value: String) extends Block {
  val values = List("earliest", "latest", "pending")
  def isValid: Boolean = values.contains(value.toLowerCase)
}
case class BlockNumber(value: Int) extends Block