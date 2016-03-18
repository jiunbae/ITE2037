import java.util.Scanner;

public class Program {
	/**
	 * 369������ ȥ�� �����ϴ� Ʋ�� ������ ���� �Է¹޾� ��ȯ�մϴ�.
	 * 
	 * @return �ַܼ� �Է¹��� ������ ���Դϴ�.
	 */
	static int GetLastNumber() {
		int result;

		// �ܼ� �Է��� ���� Scanner �ν��Ͻ� ����
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		// ��ȿ�� ���ڸ� �Է¹��� ������ �ݺ�
		while (true) {
			System.out.print("�������� Ʋ�� ����(1 ~ 99)�� �Է��ϼ���>");

			result = scanner.nextInt();

			if (result >= 1 && result <= 99) {
				break;
			}

			System.out.println("Error. �Է��� ���� ��ȿ���� �ʽ��ϴ�.");
		}

		// while���� �������Դٴ� ���� ��ȿ�� ���ڸ� �Է¹޾Ҵٴ� ���̹Ƿ� ��� ��ȯ
		return result;
	}

	static void Say(int currentNumber, int lastNumber) {
		// ���ڸ� ���� ���ڸ� �� ���
		int shipJari = currentNumber / 10;
		int illJari = currentNumber % 10;

		// �̹��� �ڼ��� �� �� �ľ� �ϴ��� ���
		int baksuCount = 0;

		if (shipJari == 3 || shipJari == 6 || shipJari == 9) {
			++baksuCount;
		}

		if (illJari == 3 || illJari == 6 || illJari == 9) {
			++baksuCount;
		}

		// �̹��� Ʋ���� �Ѵٸ�(������ ���ڶ��) �ڼ� Ƚ�� ����
		if (currentNumber == lastNumber) {
			// �̹��� ���� �ڼ��� �� �ľ� �ߴٸ� ħ
			if (baksuCount == 0) {
				++baksuCount;
			}

			// �̹��� ���� �ڼ��� �ľ� �ߴٸ� Ƚ���� �ϳ� ����
			else {
				--baksuCount;
			}
		}

		// ����(���۵�) �ڼ� Ƚ���� ���� ���

		// �ڼ��� �ľ� �Ѵٸ� �� Ƚ����ŭ ħ
		if (baksuCount != 0) {
			while (baksuCount != 0) {
				System.out.print("¦");

				--baksuCount;
			}
		}

		// ���ڸ� ���ؾ� �Ѵٸ� ����
		else {
			// 20 �̻��� ���� ���ڸ� ���� ���ؾ� ��
			if (shipJari >= 2) {
				Say_Digit(shipJari);
			}

			// 10 �̻��� ���� '��'�� ���ؾ� ��
			if (shipJari >= 1) {
				System.out.print("��");
			}

			// ���ڸ� ���� 0�� �ƴ϶�� ���ڸ� ���� ���ؾ� ��
			// Say_Digit()�� '0'�� �������� �����Ƿ� �� ȣ���ص� ��
			Say_Digit(illJari);
		}

		// �� �������� println()�� argument ���� ȣ���Ͽ� ���� ���
		System.out.println();
	}

	/**
	 * 10���� �ڸ��� �ϳ��� ���� �츮�� ǥ���� �ֿܼ� ����մϴ�. ��ȿ ����[1~9] �̿��� �Է��� ������ �ƹ� �͵� ���� �ʽ��ϴ�.
	 * 
	 * @param digit
	 *            ����� �ڸ���[1~9]�Դϴ�.
	 */
	static void Say_Digit(int digit) {
		// �Է¹��� ���ڿ� ���� �츮�� ǥ���� ���
		switch (digit) {
		case 1:
			System.out.print("��");
			break;
		case 2:
			System.out.print("��");
			break;
		case 3:
			System.out.print("��");
			break;
		case 4:
			System.out.print("��");
			break;
		case 5:
			System.out.print("��");
			break;
		case 6:
			System.out.print("��");
			break;
		case 7:
			System.out.print("ĥ");
			break;
		case 8:
			System.out.print("��");
			break;
		case 9:
			System.out.print("��");
			break;
		}
	}

	public static void main(String[] args) {
		int lastNumber;

		// Ʋ�� ���� �Է¹ޱ�
		lastNumber = GetLastNumber();

		// ȥ�� ������ ������ �����ϱ�(1���� 'Ʋ�� ����'����)
		for (int currentNumber = 1; currentNumber <= lastNumber; currentNumber++) {
			Say(currentNumber, lastNumber);
		}

		// Ʋ�� ���ڱ��� Say()�� ȣ���ߴٸ� �ݵ�� 'Ʋ����'�״� ������ �й� �޽��� ��� �� ����
		System.out.println("������ �й� �޽���");
	}
}