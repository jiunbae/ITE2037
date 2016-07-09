
# 오브젝트
오브젝트는 게임에 있어서 가장 중요한 구성요소이자 전부라고 말할 수 있습니다.
모든 보이는 물체는 Object로 표현되며 우리는 이것을 직접 만들고 수정할 수 있습니다.

## 종류
오브젝트는 크게 일반 오브젝트와 스크립트 오브젝트로 나눌 수있습니다.

- sprite: 일반 오브젝트는 일반적인 오브젝트들 입니다. 실제 사물을 나타낼 수 있습니다.
- script: 스크립트 오브젝트는 스크립트를 위한 오브젝트 입니다.

## Sprite Object
sprite 오브젝트는 다양한 속성들을 가지고 있습니다. 이 속성들을 이용해 각각 오브젝트를 독특하게 만들 수 있습니다.

- name: 객체의 이름을 설정합니다. 이름은 영문과 숫자만 가능합니다.
- x, y, z: 위치를 설정합니다. z값은 어떤 오브젝트가 앞(뒤)에 있나를 결정합니다. 이 값은 중심값입니다.
- vx, vy, ax ay: 속도, 가속도를 지정합니다.
- w, h: 크기를 결정합니다.
- enable: true/false로 활성화할것인지 결정할 수 있습니다. false라면 모든 반응을 하지 않습니다.
- visible: true/false로 보여질것인지 아닌지를 설정할 수 있습니다.
- collision: true/false로 충돌할것인지 아닌지를 설정합니다.
- physics: true/false로 물리효과를 적용하거나 하지않습니다.
- absolute: false/true로 화면에 고정될것인지 설정할 수 있고, 객체이름으로 객체에 고정시킬 수 있습니다.
- texture: 보여질 이미지의 경로를 적습니다. *$res$이후 경로만 적으면 됩니다.*
- script: 스크립트를 설정합니다. 이 스크립트의 target이 비어있다면 target을 자신으로 합니다.

#### 특별한 오브젝트
우리는 게임을 하기위해 플레이어가 조정할 두 개의 특별한 sprite 오브젝트를 만들어야 합니다.
어떤 맵 파일이던 "me"와 "you"를 이름으로 갖는 스프라이트를 만들어야 정상적인 게임이 진행될 수 있습니다.
```
<sprite name="me" w="32" h="32" texture="image"></sprite>
<sprite name="you" w="32" h="32" texture="image"></sprite>
```

#### 부모와 자식
sprite 오브젝트는 자식들을 가질 수 있습니다. 이 자식들은 기본적으로 몇가지 특성들이 있습니다.

- z값을 부모에게서 상속받습니다.
- absolute의 초기값은 부모입니다.
- 언제나 부모의 enable, visible을 상속받습니다. (부모의 enable이 false가 되면 자식도 모두 false가 됩니다.)
- 만약 script가 자식이라면 target의 초기값은 부모입니다.

<hr>

## Script Object
script 오브젝트는 아래의 속성을 가질 수 있습니다. <br>
*event attribute가 만족할때 해당 스크립트가 실행됩니다.*

- name: 스크립트의 이름을 설정합니다.
- target: 타겟의 이름을 설정합니다.
- event: 아래 이벤트들을 지정할 수 있습니다.
	- never: 직접호출로만 가능한 이벤트입니다.
	- always: 계속 호출되는 이벤트입니다.
	- object@enter: 캐릭터가 오브젝트에 닿기 시작할때를 나타냅니다.
	- object@leave: 캐릭터가 오브젝트에 떠나기 시작할때를 나타냅니다.
	- object@join: 캐릭터가 오브젝트에 닿아있을때를 나타냅니다.
	- object@interact: 캐릭터가 오브젝트와 상호작용할떄를 나타냅니다. <br> *object@join 하면서 key@interact 할떄와 같습니다.*
	- mouse@enter: 마우스가 객체 위에 들어올때를 나타냅니다.
	- mouse@leave: 마우스가 객체 위를 떠날때를 나타냅니다.
	- mouse@join: 마우스가 객체 위에 있을떄를 나타냅니다.
	- mouse@interact: 마우스가 객체를 클릭했을때를 나타냅니다.
- attribute: 타겟의 속성 상태를 지정합니다. 비워두면 언제나 참입니다. 예) attribute="visible=true"
- delay: 1ms단위의 딜레이입니다. delay="1000"이라면 1초후 실행  

#### 특수한 명령
sciprt 오브젝트는 몇가지 특수한 호출을 할 수 있습니다.

- fin: 게임을 클리어 했을때 호출하면 됩니다.
- over: 게임을 실패했을 때 호출하면 됩니다.

#### 응용하기
script와 sprite오브젝트를 이용하면 여러 효과를 만들 수 있습니다. 아래의 예제들은 간단한 몇가지 용례를 소개합니다.

##### 버튼
sprite에 mouose event를 잘이용하면 버튼을 만들 수 있습니다.

```
<sprite name="button" x="100" y="50" w="100" h="20" texture="btn.png">
	<script event="mouse@hover">
		button.texture="btn_hover.png";
	</script>
	<script event="mouse@leave">
		button.texture="btn.png";
	</script>
	<script event="mouse@click">
		fin;
	</script>
</sprite>
```