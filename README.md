# BananaRama
[![alt text](https://travis-ci.org/cr0wbar/Bananarama.svg?branch=master "Build Status")](https://travis-ci.org/cr0wbar/Bananarama)
### Overview
BananaRama provides a simple and extensible CRUD framework to access the persistency layer in a modular and scalable way. It aims to provide simple APIs to query entities without the client really knowing where those entities are stored.

This is an example of how the client code looks when entities are managed using BananaRama.

```java
public class Crud {
    
    public void test() {

	BananaRama bananarama = new BananaRama();

        //Create entry on persistency layer
        Entry entry = new Entry("testKey","testVal");
        bananarama.create(Entry.class).from(Stream.of(entry));
	
        //Read entry
        Entry entry = bananarama.read(Entry.class).all().findAny().get();        

        //Update for previous entry
        Entry entryUpdated = new Entry("testKey","testValUpdated");       
        bananarama.update(Entry.class).from(Stream.of(entryUpdated));
        
        //Delete entry
        bananarama.delete(Entry.class).from(Stream.of(entryUpdated));
        
    }
}
```
### Built-In persistency layer support
BananaRama natively supports the following frameworks/persistency layers
1. [JDBC] (#jdbc) - fast POJOs to SQL and viceversa.
2. [JPA](#jpa) - Manage JPA entities with BananaRama.
3. [CQEngine](#cqengine) - Integrated support for CQEngine indexed collection on top of other persistency layers or standalone.

## Simplest Example
Integration of entities in BananaRama is done in two steps.
First we need to create a class which implements the  `Adapter` interface. This will be the class that will actually manage the entities, either by accessing the persistency layer or calling other adapters. 

```
public final class NoOpAdapter implements Adapter<Object>{
    //A really simple adapter which does nothing :)
    @Override
    public <T> CreateOperation<T> create(Class<T> clazz) {
        return new CreateOperation<T>() {
            @Override
            public CreateOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public CreateOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
    @Override
    public <T> ReadOperation<T> read(Class<T> clazz) {
        return new ReadOperation<T>() {
            @Override
            public Stream<T> all() {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> all(QueryOptions options) {
                return Stream.empty();
            }
            
            @Override
            public <Q> Stream<T> where(Q whereClause) {
                return Stream.empty();
            }
            
            @Override
            public <Q> Stream<T> where(Q whereClause, QueryOptions options) {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> fromKeys(List<?> keys) {
                return Stream.empty();
            }
            
            @Override
            public Stream<T> fromKeys(List<?> keys, QueryOptions options) {
                return Stream.empty();
            }
            
        };
    }
    
    @Override
    public <T> UpdateOperation<T> update(Class<T> clazz) {
        return new UpdateOperation<T>() {
            
            @Override
            public UpdateOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public UpdateOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
    @Override
    public <T> DeleteOperation<T> delete(Class<T> clazz) {
        return new DeleteOperation<T>() {
            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus) {
                return this;
            }
            
            @Override
            public <Q> DeleteOperation<T> where(Q whereClaus, QueryOptions options) {
                return this;
            }
            
            @Override
            public DeleteOperation<T> from(Stream<T> data) {
                return this;
            }
            
            @Override
            public DeleteOperation<T> from(Stream<T> data, QueryOptions options) {
                return this;
            }
        };
    }
    
}
```

Then annotate the entity you wish to manage
```java
@Banana(adapter = NoOpAdapter.class)
public class Entry {
    private String key,val;
    
    public Entry(String key,String val){
        this.val = val;
        this.key = key;
    }
    
    public String getValue(){
        return val;
    }

    public String getKey(){
        return key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
```
The `@Banana` annotation  takes care of redirecting BananaRama to the
class in charge of CRUD operations for the `Entry` type. In this example all CRUD operations will then be delegated to the `NoOpAdapter`. The basic idea is that the client code contains no references to the adapter, therefore the persistency layer can be swapped but the client code won't change.

### Adapter dependencies
It is possible to chain adapters in case some entities depend on other entities. A list of adapter types can be provided using the `@BananaRamaAdapter` annotation: BananaRama will load the required adapters beforehand and will pass it to the annotated adapter's constructor so that they can be used to do CRUD operations on child entities. See the following example
```java
@BananaRamaAdapter(requires = {SubAdapter.class,BananaRama.class})
public class MyAdapter extends NoOpAdapter{
    private BananaRama parent;
    private SubAdapter sub;

    public MyAdapter(SubAdapter sub, BananaRama parent){//Order of args
        this.parent = parent;                           //is the same in the annotation
        this.sub = sub;
    }
}
```
Note that since the `BananaRama` class itself implements an adapter (i.e the root adapter) it can also be passed as a requirement in order to cascade CRUD operations on child entities without knowing the specific adapter for those entities. All adapters are stored in the instance of `BananaRama` that was used to instantiate them.
### Explicit usage of adapters
 Although you are discouraged to do so, BananaRama also allows to use specific adapters for a specific entity with the `using` method
```java
 //will return Optional<Entry> retrieved by an instance of MyAdapter.class
 bananarama.using(MyAdapter.class).read(Entry.class).all().findFirst();
```
### Thread-safety
As a general rule, take into account the following table for thread-safety of BananaRama's components

 Component | Thread-Safe
--- | ---
Adapter | &#10004;
Any CRUD operation | &#10006;
 
## Built-In adapters
### JDBC
TODO
### JPA
TODO
### CqEngine
TODO
### Magic (DTO to Object and viceversa)
TODO


