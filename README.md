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

* [JDBC] (#jdbc) - fast POJOs to SQL and viceversa.
* [JPA](#jpa) - Manage JPA entities with BananaRama.
* [CQEngine](#cqengine) - Integrated support for CQEngine indexed collection on top of other persistency layers or standalone.

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

            @Override
            public void close() throws Exception {
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

            @Override
            public void close() throws Exception {
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

            @Override
            public void close() throws Exception {
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

            @Override
            public void close() throws Exception {
            }
        };
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
 
## Built-In Adapters
### JDBC
The Jdbc adapter is designed to do fast and simple CRUD operations using JDBC in conjunction with any driver you want to use. So far it has been tested successfully on H2 and PostgreSQL drivers.

First of all implement your adapter which extends the `SQLAdapter` class
```java
public class MySqlAdapter extends SqlAdapter {

    public MySqlAdapter() {
        super(DataSourceFactory.getDataSource());
    }

}
```
Then you can automatically map database tables to classes by annotating them 
```java
@Banana(adapter = MySqlAdapter.class)
@Table(name = "pojo")
@Convert(type = LocalDateTime.class,with = LocalDateTimeConverter.class)
public class Pojo {

    @Id 
    private int id;

    @Column(name = "name")
    private String laBel;
    
    private Double xyz;
    
    @ConvertWith(MapConverter.class)
    private Map<String,String> attrs;
    
    public int getId() {
        return id;
    }

    public void setId(int idno) {
        this.id = idno;
    }

    public String getLaBel() {
        return laBel;
    }

    public void setLaBel(String laBel) {
        this.laBel = laBel;
    }

    public Double getXyz() {
        return xyz;
    }

    public void setXyz(Double xyz) {
        this.xyz = xyz;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }
}
```
All annotation in the previous example are part of BananaRama. Here is a quick overview of them:

*  `@Id` identifies key fields. It may be used on multiple fields, but use it carefully: if fields `a` and `b` of type `A` and `B` define the object's identity, then the `List` passed to the `fromKeys` method should contain alternating values of type `A` and `B`.
* `@Column` and `@Table` work very similarly to the the equivalent JPA annotations, with some extra features explained in the javadoc.
* `@Convert` and `@ConvertWith` provide the adapter with custom SQL types translators. The first is for defining global translators for the annotated class, the latter is for setting a translator for a specific field and overrides any global translator.  

### JPA
Implement a custom adapter in order to access a specific persistency unit.
```java
public class MyJpaAdapter extends AbstractJpaAdapter{
    
    public MyJpaAdapter() {
        super("my_awesome_unit");//must match a persistency unit name in persistence.xml
    }

}
```
Then simply annotate with `Banana( adapter = MyJpaAdapter.class)` any class annotated with `@Entity` and other annotations which are part of JPA and use it with BananaRama.

### CqEngine
TODO
### Magic (DTO to Object and viceversa)
TODO


