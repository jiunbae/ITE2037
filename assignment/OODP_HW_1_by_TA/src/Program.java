import java.util.Scanner;

public class Program {
	/**
	 * 369게임을 혼자 진행하다 틀릴 마지막 수를 입력받아 반환합니다.
	 * 
	 * @return 콘솔로 입력받은 마지막 수입니다.
	 */
	static int GetLastNumber() {
		int result;

		// 콘솔 입력을 받을 Scanner 인스턴스 생성
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		// 유효한 숫자를 입력받을 때까지 반복
		while (true) {
			System.out.print("마지막에 틀릴 숫자(1 ~ 99)를 입력하세요>");

			result = scanner.nextInt();

			if (result >= 1 && result <= 99) {
				break;
			}

			System.out.println("Error. 입력한 값이 유효하지 않습니다.");
		}

		// while문을 빠져나왔다는 것은 유효한 숫자를 입력받았다는 것이므로 즉시 반환
		return result;
	}

	static void Say(int currentNumber, int lastNumber) {
		// 십자리 수와 일자리 수 계산
		int shipJari = currentNumber / 10;
		int illJari = currentNumber % 10;

		// 이번에 박수를 몇 번 쳐야 하는지 계산
		int baksuCount = 0;

		if (shipJari == 3 || shipJari == 6 || shipJari == 9) {
			++baksuCount;
		}

		if (illJari == 3 || illJari == 6 || illJari == 9) {
			++baksuCount;
		}

		// 이번에 틀려야 한다면(마지막 숫자라면) 박수 횟수 조작
		if (currentNumber == lastNumber) {
			// 이번에 원래 박수를 안 쳐야 했다면 침
			if (baksuCount == 0) {
				++baksuCount;
			}

			// 이번에 원래 박수를 쳐야 했다면 횟수를 하나 줄임
			else {
				--baksuCount;
			}
		}

		// 계산된(조작된) 박수 횟수에 따라 출력

		// 박수를 쳐야 한다면 그 횟수만큼 침
		if (baksuCount != 0) {
			while (baksuCount != 0) {
				System.out.print("짝");

				--baksuCount;
			}
		}

		// 숫자를 말해야 한다면 말함
		else {
			// 20 이상의 수는 십자리 수를 말해야 함
			if (shipJari >= 2) {
				Say_Digit(shipJari);
			}

			// 10 이상의 수는 '십'을 말해야 함
			if (shipJari >= 1) {
				System.out.print("십");
			}

			// 일자리 수가 0이 아니라면 일자리 수를 말해야 함
			// Say_Digit()은 '0'을 발음하지 않으므로 걍 호출해도 됨
			Say_Digit(illJari);
		}

		// 다 끝났으니 println()을 argument 없이 호출하여 엔터 출력
		System.out.println();
	}

	/**
	 * 10진수 자리수 하나에 대한 우리말 표현을 콘솔에 출력합니다. 유효 범위[1~9] 이외의 입력이 들어오면 아무 것도 하지 않습니다.
	 * 
	 * @param digit
	 *            출력할 자리수[1~9]입니다.
	 */
	static void Say_Digit(int digit) {
		// 입력받은 숫자에 대한 우리말 표현을 출력
		switch (digit) {
		case 1:
			System.out.print("일");
			break;
		case 2:
			System.out.print("이");
			break;
		case 3:
			System.out.print("삼");
			break;
		case 4:
			System.out.print("사");
			break;
		case 5:
			System.out.print("오");
			break;
		case 6:
			System.out.print("육");
			break;
		case 7:
			System.out.print("칠");
			break;
		case 8:
			System.out.print("팔");
			break;
		case 9:
			System.out.print("구");
			break;
		}
	}

	public static void main(String[] args) {
		int lastNumber;

		// 틀릴 숫자 입력받기
		lastNumber = GetLastNumber();

		// 혼자 삼육구 게임을 진행하기(1부터 '틀릴 숫자'까지)
		for (int currentNumber = 1; currentNumber <= lastNumber; currentNumber++) {
			Say(currentNumber, lastNumber);
		}

		// 틀릴 숫자까지 Say()를 호출했다면 반드시 '틀렸을'테니 적절한 패배 메시지 출력 후 종료
		System.out.println("적절한 패배 메시지");
	}
}
