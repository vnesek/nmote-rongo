nmote-rongo
===========

*Java 1.8 reactive driver for mongodb*

Rongo is mashup of mostly code transplated from jongo and mongo-jackson-codec with a bit of
mongo async driver adapted to reactive streams in a mix.

I really like what jongo is doing with jackson + bson4jackson serializing mongo documents.
Adopting existing jongo to new mongo async driver and reactive streams seamed really hard, so I
repackaged important bits.

*Warning:* work-in-progress, API isn't stable nor complete.

Features
--------

* Completely asynchronous operation with project reactor and mongo async driver
* RongoCollection wraps MongoCollection giving Flux<T> and Mono<T> results
* Jackson with bson4jackson for serializing/deserializing objects (Optional)
* Templated query binding with positional (:1, ...) and named parameters (:name) (Optional)

Usage
-----

Wrap MongoCollection in RongoCollection and find all instances and print id. Use jackson
for mapping Instance objects.

```java

	import com.mongodb.async.client.MongoCollection;
	import com.nmote.rongo.Rongo;
	import com.nmote.rongo.RongoCollection;

	MongoCollection<Instance> collection = db.getCollection("instance", Instance.class);
	RongoCollection<Instance> instances = Rongo.from(collection).withJacksonCodecRegistry();

	instances.find()
	    .flux()
	    .map(Instance::getId)
	    .subscribe(System.out::println);
```

Find a single object by id. Query by mongo model Filters.

```java

	import static com.mongodb.client.model.Filters.*;
	import reactor.core.publisher.Mono;

	Mono<Instance> instance = instances.find(and(eq("_id", new ObjectId("55e65373e4b018aebdde9d2c")))).first();
```

Find a single object by id. Query by rongo binding.

```java

	import static com.mongodb.client.model.Filters.*;
	import reactor.core.publisher.Mono;

	Mono<Instance> instance = instances.find("{ _id: ':1' }", new ObjectId("55e65373e4b018aebdde9d2c")).first();
```


Add to Your's Project
---------------------

If you use maven for dependency management, add following snippet to pom.xml:

```xml

	<dependencies>
		...

		<dependency>
			<groupId>com.nmote.rongo</groupId>
			<artifactId>nmote-rongo</artifactId>
			<version>0.8</version>
		</dependency>

	</dependencies>
```

Changes
-------

* 0.5 Initial release

Building
--------
To produce nmote-rongo.jar you will need apache maven installed. Run:

> mvn clean package


References
----------

* [mongo-jackson-codec](https://github.com/ylemoigne/mongo-jackson-codec)
* [jongo](https://github.com/bguerout/jongo)
* [project reactor](https://projectreactor.io/)



Author Contact and Support
--------------------------

For further information please contact
Vjekoslav Nesek (vnesek@nmote.com)
