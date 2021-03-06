[![Build Status](https://travis-ci.org/web3scala/web3scala.svg?branch=master)](https://travis-ci.org/web3scala/web3scala)
[![codecov](https://codecov.io/gh/web3scala/web3scala/branch/master/graph/badge.svg)](https://codecov.io/gh/web3scala/web3scala)

# web3scala
_web3scala_ allows seamless integration with [Ethereum](https://www.ethereum.org) blockchain, using Scala programming 
language.

Lightweight, efficient, using Scala idioms, it spares you the trouble of writing own low-level code controlling the 
work of Ethereum nodes.  

## Features

* Complete implementation of [JSON-RPC](https://github.com/ethereum/wiki/wiki/JSON-RPC) Ethereum client API over HTTP
* Support for [Whisper v5](https://github.com/ethereum/go-ethereum/wiki/Whisper) (work in-progress)

## Getting started

#### SBT

    libraryDependencies += "org.web3scala" % "core" % "0.1.0"

#### Ethereum client

    $ geth --rpcapi personal,db,eth,net,web3,shh --shh --rpc --testnet

#### Sending requests

```scala
  val service = new Service
  
  // synchronous call (returns Either[Error, Response])
  service.web3ClientVersion match {
    case Left(e) => println("Error: " + e.error)
    case Right(s) => println("Client Version: " + s.result)
  }

  // asynchronous call (returns a future wrapped in AsyncResponse)
  val future = service.asyncWeb3ClientVersion.future
  val response = future().as[GenericResponse]
  response.result match {
    case Some(s) => println(s)
    case None => println(response.error)
  }
```

#### Stacking futures

Assuming you have three Ethereum wallets:

```scala
  val rq1 = ("0x1f2e3994505ea24642d94d00a4bcf0159ed1a617", BlockName("latest"))
  val rq2 = ("0xf9C510e90bCb47cc49549e57b80814aE3A8bb683", BlockName("pending"))
  val rq3 = ("0x902c4fD71e196E86e7C82126Ff88ADa63a590d22", BlockNumber(1559297))
```

and want to choose one with most Ether in it:

```scala
  val result = highestBalance(rq1, rq2, rq3)

  println("Highest Balance: " + result())
```

Here's how to achieve that with web3scala:

```scala
   def highestBalance(requestParams: (String, Block)*) = {
     
     // execute async requests
     val responses =
       for (requestParam <- requestParams)
         yield requestParam._1 -> service.asyncEthGetBalance(requestParam._1, requestParam._2)
 
     // parse responses
     val futures =
       for (response <- responses)
         yield for (json <- response._2.future)
           yield response._1 -> Utils.hex2long((json \ "result").extract[String])
 
     // select max balance and return corresponding address
     for (future <- Future.sequence(futures))
       yield future.maxBy(_._2)._1
   }
```

Result:

    $ Highest Balance: 0x1f2e3994505ea24642d94d00a4bcf0159ed1a617

The code is non-blocking on I/O at any point, and http requests execution fully parallelized.
You'll find a working sample in the _examples_ directory.

## Dependencies

The library has following runtime dependencies:

* [Dispatch Reboot](https://dispatchhttp.org) for asynchronous HTTP interaction
* [Json4s-Jackson](http://json4s.org) for JSON parsing/generation
* [jackson-module-scala](https://github.com/FasterXML/jackson-module-scala) to support Scala-specific datatypes