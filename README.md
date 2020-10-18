# 6 REST 서비스 생성하기

## 6.1 Rest 컨트롤러 작성하기

**SPA**<br>
SPA에서는 프레젠테이션 계층이 백엔드 처리와 거의 독립적이므로, 백엔드 기능은 같은 것을 사용하면서 사용자 인터페이스만 다르게(ex. 모바일 어플리케이션) 개발할 수 있다.
<br><br>
**스프링 MVC의 HTTP 요청-처리 어노테이션**
|애노테이션|HTTP 메서드|용도|
|---|---|---|
|@GetMapping|HTTP GET 요청|리소스 데이터 읽기|
|@PostMapping|HTTP POST 요청|리소스 생성하기|
|@PutMapping|HTTP PUT 요청|리소스 변경하기|
|@PatchMapping|HTTP PATCH 요청|리소스 변경하기|
|@DeleteMapping|HTTP DELETE 요청|리소스 삭제하기|
|@RequestMapping|다목적 요청 처리이며, HTTP 메서드가 method 속성에 지정된다.||
CRUD와 HTTP메서드는 완벽하게 1:1 대응은 아니지만 대응시키는 방법으로 사용된다.
<br>

### 6.1.1 서버에서 데이터 가져오기
**리스트 6.1 - 최근 타코들의 내역을 보여주는 앵귤러 컴포넌트**
```typescript
import { Component, OnInit, Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'recent-tacos',
  templateUrl: 'recents.component.html',
  styleUrls: ['./recents.component.css']
})

@Injectable()
export class RecentTacosComponent implements OnInit {
  recentTacos: any;

  constructor(private httpClient: HttpClient) { }

  ngOnInit() {
    this.httpClient.get('http://localhost:8080/design/recent') // <1>
        .subscribe(data => this.recentTacos = data);
  }
```

<br>

`ngOnInit()` 메서드에서 RecentTacosComponent는 주입된 Http모듈을 사용하여 `http://localhost:8080/design/recent`에 대한 HTTP 요청을 수행한다. 이 경우 recentTacos 모델 변수로 참조되는 타코들의 내역이 response에 포함된다. 그리고 recents.component.html의 뷰에서는 브라우저에 나타나는 HTML로 모델 데이터를 보여준다. <br>
다음은 리스트 6.1의 앵귤러 컴포넌트가 수행하는 /design/recent의 GET요청을 처리하는 엔드포인트가 필요하다.

<br>

**리스트6.2 - 타코 디자인 API 요청을 처리하는 REST Controller**
```java
@RestController
@RequestMapping(path="/design", produces="application/json")// /design 경로의 요청 처리
@CrossOrigin(origins="*") // 서로 다른 도메인 간의 요청을 허용한다.
public class DesignTacoController {
	private TacoRepository tacoRepo;
	
	@Autowired
	EntityLinks entityLinks;
	
	public DesignTacoController(TacoRepository tacoRepo){
		this.tacoRepo = tacoRepo;
	}
	
	@GetMapping("/recent")
	public Iterable<Taco> recentTacos(){
		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		return tacoRepo.findAll(page).getContent();
	}
}
```
<br>

`@RestController` 애노테이션은 `@Controller`나 `@Service`와 같이 스테레오타입 애노테이션이므로 이 애노테이션이 지정된 클래스를 스프링의 컴포넌트 검색으로 찾을 수 있다. 즉 `@RestController`는 컨트롤러의 모든 Http 요청 처리 메서드에서 HTTP Response Body에 직접 쓰는 값을 반환한다는 것을 스프링에 알려준다. (뷰로 보여줄 값을 반환하는 일반적인 `@Controller` 와는 다르다). 따라서 뷰를 통해 HTML로 변환되지 않고 직접 HTTP 응답으로 브라우저에 전달되어 나타난다. <br>
리스트 6.2의 DesignTacoController에는 /design 경로의 요청을 처리하도록 @RequestMapping 애노테이션이 지정되었고, recentTacos() 메서드에는 /recent 경로의 GET요청을 처리하는 `@GetMapping`이 지정되었다. 따라서 recentTacos() 메서드에서는 /design/recent 경로의 GET 요청을 처리해야한다. 이것은 리스트 6.1의 앵귤러 코드가 실행될 때 필요한 기능이다. <br>
`@RequestMapping`애노테이션에는 produces 속성("application/json")도 설정되어있다. 이것은 Accept 헤더에 "application/json"이 포함된 요청만을 DesignTacoController의 메서드에서 처리한다는 것을 나타낸다. 이 경우 응담 결과는 JSON형식이 되지만, produces 속성의 값은 String 배열로 저장된다. XML로 출력하고자 할 때는 다음과 같이 수정하면 된다.
```java
@RequestMapping(path="/design", produces={"application/json", "text/xml"})
```
<br>

리스트 6.1의 코드는 리스트 6.2의 API와 별도의 도메인에서 실행중이므로 앵귤러 클라이언트에서 리스트 6.2 API를 사용하지 못하게 막는다. 이런 제약은 CORS(Cross-Origin Resource Sharing) 헤더를 포함시켜 극복할 수 있으며 스프링에서는 @CrossOrign 애노테이션을 지정하여 쉽게 적용할 수 있다.
<br><br>
타코 ID로 특정 타코만 가져오는 엔드포인트를 제공하려면 메서드 경로에 플레이스홀더 변수를 지정하면 된다
```java
@GetMapping("/{id}")
public Taco tacoById(@PathVariable("id") Long id) {
	Optional<Taco> optTaco = tacoRepo.findById(id);
	if(optTaco.isPresent())[
		return optTaco.get();
	}
	return null;
}
```
<br>
여기서 {id} 부분이 플레이스홀더이며, `@PathVariable`에 의해 {id} 플레이스홀더와 대응되는 id 매개변수에 실제 값이 지정된다. 
<br><br>
해당 ID와 일치하지 않는다면 null을 반환하지만 이는 좋은 방법이 아니다. null을 반환하더라도 HTTP 200(OK) 상태코드를 클라이언트가 받기 때문이다. 따라서 이때는 다음과 같이 HTTP 404(NOT FOUND) 상태 코드를 응답하는게 좋다.
```java
	@GetMapping("/{id}")
	public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
		Optional<Taco> optTaco = tacoRepo.findById(id);
		if(optTaco.isPresent()) {
			return new ResponseEntity<>(optTaco.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}
```
<br>
<br>
### 6.1.2 서버에 데이터 전송하기

### 6.1.3 서버의 데이터 변경하기

### 6.1.4 서버의 데이터 삭제하기

## 6.2 하이퍼미디어 사용하기
지금까지는 클라이언트가 API의 URL을 알아야 동작했다. 예를 들어, /design/recent에 GET요청을 하여 최근 생성된 리스트를 얻을 수 있다. 하지만 URL 스킴이 변경될 경우 GET 요청을 할 URL을 변경해야한다. 이를 방지하기 위해 하이퍼미디어를 사용한다. <br>
의존성을 추가하기위해 pom.xml에 아래와 같이 추가한다. 최근 버전은 Resources, Resource 등의 클래스 명이 변경되었다. <br>
<br>
|변경 전|변경 후|
|---|---|
|ResourceSupport|RepresentationModel|
|Resource|EntityModel|
|Resources|CollectionModel|
|PagedResources|PagedModel|
<br>
최근 생성된 타코리스트에 하이퍼링크를 추가하기위해 리스트 6.2의 코드를 아래와 같이 수정하였다.<br>
**리스트 6.4 리소스에 하이퍼링크 수정하기**
```java
	  @GetMapping("/recent")
	  public CollectionModel<EntityModel<Taco>> recentTacos() {
		  PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		  List<Taco> tacos = tacoRepo.findAll(page).getContent();
		  
		  CollectionModel<EntityModel<Taco>> recentResources = CollectionModel.wrap(tacos);
		  recentResources.add(new Link("http://localhost:8080/design/recent", "recents"));
		  return recentResources;
	  }
```
<br>
그러나 Resources 객체를 반환하기 전에 이름이 recents이고 URL이 http://localhost:8080/design/recent인 링크를 추가한다. 따라서 API 요청에서 반환되는 리소스에 다음의 JSON 코드(`"_links":{"recents":{"href":"http://localhost:8080/design/recent"}}`)가 포함된다.<br>
```JSON
{
	"_embedded":
		{"tacoList":[
			{"id":1,"createdAt":"2020-10-17T04:05:05.026+00:00","name":"10100101010101","ingredients":[
				{"id":"FLTO","name":"Flour Tortilla","type":"WRAP"},{"id":"CHED","name":"Cheddar","type":"CHEESE
				{"id":"SLSA","name":"Salsa","type":"SAUCE"}
			]}
		]},
	"_links":{"recents":{"href":"http://localhost:8080/design/recent"}}
}
```
<br>
1차로 수정되었지만 recents 링크에 지정한 하드코딩된 URL을 수정해야한다. 개발용 컴퓨터에서만 사용하지 않고 배포시에도 다른 URL을 사용하기 때문에 좋지 않은 방법이기 떄문이다. 
<br>
**리스트 6.5 도메인 데이터와 하이퍼링크 리스트를 갖는 타코 EntityModel**
```java
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
...

	  @GetMapping("/recent")
	  public CollectionModel<EntityModel<Taco>> recentTacos() {
		  PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		  List<Taco> tacos = tacoRepo.findAll(page).getContent();
		  
		  CollectionModel<EntityModel<Taco>> recentCollectionModel = CollectionModel.wrap(tacos);
		  recentCollectionModel.add(WebMvcLinkBuilder.linkTo(methodOn(DesignTacoController.class).recentTacos()).withRel("recents"));
		  return recentCollectionModel;
	  }
```
<br>
위와 같이 수정하면 methodOn을 통해 DesignTacoController를 인자로 받아 recentTacos() 메서드를 호출할 수 있게 한다.

<br>

### 6.2.2 RepresentationModel 생성하기
다음으로는 recentResources 리스트에 포함된 각 타고 리소스에 대한 링크를 추가해야한다. 반복 루프를 통해 `CollectionModel` 객체가 가지는 `EntityModel<Taco>` 요소 마다 Link를 추가하는 방법이 있지만 API 코드마다 루프를 실행하는 코드가 있어야 하므로 번거롭기에 다른 전략을 사용한다. `EntityModel` 대신 `RepresentationModel`을 사용하는 것이다. <br>
**리스트 6.6 타코 리소스를 구성하는 리소스 어셈블러**
```java
package tacos.web.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import tacos.Taco;
import tacos.web.DesignTacoController;

public class TacoRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Taco, TacoRepresentatinModel> {
	public TacoRepresentationModelAssembler() {
		super(DesignTacoController.class, TacoRepresentatinModel.class);
	}
	
	@Override
	protected TacoRepresentatinModel instantiateModel(Taco taco) {
		// TODO Auto-generated method stub
		return new TacoRepresentatinModel(taco);
	}
	
	@Override
	public TacoRepresentatinModel toModel(Taco taco) {
		// TODO Auto-generated method stub
		return createModelWithId(taco.getId(), taco);
	}
	
}
```
<br>
TacoRepresentationModelAssembler의 기본 생성자에는 슈퍼 클래스인 RepresentationModelAssemblerSupport의 기본 생성자를 호출하며, 이때 TacoRepresentationModel을 생성하면서 만들어지는 링크에 포함되는 URL의 기본경로를 설정하기 위해 DesignTacoController를 사용한다. <br>
instatiateModel() 메서드는 인자로 전달된 Taco 객체로 TacoRepresentationModel 인스턴스를 생성하도록 오버라이드 되었다. TacoRepresentationModel이 기본 생성자를 갖고 있다면 이 메서드는 생략할 수 있다. 그러나 여기서는 Taco객체로 TacoRepresentationModel 인스턴스를 생성해야 하므로 오버라이드해야 한다. <br>
마지막으로 toModel() 메서드는 RepresentationModelAssemblerSupport로부터 상속받을 때 반드시 오버라이드 해야한다. 여기서는 TacoResource 인스턴스를 생성하면서 Taco 객체의 id 속성 값으로 생성되는 self 링크가 URL에 자동 저장된다.<br>
toModel()과 instatiateModel()이 같은 목적을 갖는 것처럼 보이지만, 약간 다르다. instantiateModel()는 Model 인스턴스만 생성하지만, toModel()는 Model 인스턴스를 생성하면서 링크도 추가한다. **내부적으로 toModel()는 instantiateMdoel()를 호출한다.** <br>
이제는 6.4의 recentTacos() 메서드를 아래처럼 변경한다. <br>
```java
	  @GetMapping("/recent")
	  public CollectionModel<TacoRepresentatinModel> recentTacos() {
		  PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		  List<Taco> tacos = tacoRepo.findAll(page).getContent();
		  
		  CollectionModel<TacoRepresentatinModel> recentCollectionModel = new TacoRepresentationModelAssembler().toCollectionModel(tacos);
		  recentCollectionModel.add(WebMvcLinkBuilder.linkTo(methodOn(DesignTacoRestController.class).recentTacos()).withRel("recents"));
		  return recentCollectionModel;
	  }
```
<br>
/design/recent에 대한 GET요청을 수행하면 아래와 같은 결과가 나온다.
```JSON
{"_embedded":
	{"tacoRepresentatinModelList":
		[
			{"name":"10100101010101",
				"createdAt":"2020-10-18T07:10:17.014+00:00",
				"ingredients":
					[
						{"id":"FLTO",
							"name":"Flour Tortilla",
							"type":"WRAP"
						},
						{"id":"GRBF",
							"name":"Ground Beef",
							"type":"PROTEIN"
						},
						{"id":"JACK",
							"name":"Monterrey Jack",
							"type":"CHEESE"
						},
						{"id":"TMTO",
							"name":"Diced Tomatoes",
							"type":"VEGGIES"
						},
						{"id":"SRCR",
							"name":"Sour Cream",
							"type":"SAUCE"
						}
					],
				"_links":
					{"self":
						{"href":"http://localhost:8080/design/2"}
					}
			}
		]
	},
	"_links":{
		"recents":
			{
				"href":"http://localhost:8080/design/recent"
			}
	}
}
```
<br>
포함된 Ingredient리스트도 IngredientRepresentationModel로 만들면 Ingredient에도 self 링크가 생성된다. 

<br>

### 6.2.3 embeded 관계 이름 짓기
위 결과를 확인하면 embedded 하단에 tacoRepresentatinModelList라는 이름이 있다. 이 이름은 `RepresentationModel` 객체가 `List<TacoRepresentatinModel>`로 부터 생성되었다는 것을 나타낸다. 그러나 TacoRepresentatinModel의 이름이 변경되면 위 JSON을 참조하고있던 다른 소스까지 변경해야한다. 따라서 아래와 같은 코드를 통해 결합도를 낮출 수 있다. <br>
```java
@Relation(value="taco", collectionRelation="tacos")
public class TacoRepresentatinModel extends RepresentationModel<TacoRepresentatinModel> {
	@Getter
	private final String name;
	
	@Getter
	private final Date createdAt;
	
	@Getter
	private final List<Ingredient> ingredients;

	public TacoRepresentatinModel(Taco taco) {
		this.name = taco.getName();
		this.createdAt = taco.getCreatedAt();
		this.ingredients = taco.getIngredients();
	}
}
```
이와 같이 설정하면 아래와 같이 나온다. <br>
```JSON
{"_embedded":
	{"tacos":
		[
		...
```
