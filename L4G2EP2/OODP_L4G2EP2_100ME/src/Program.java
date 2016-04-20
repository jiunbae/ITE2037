import java.awt.Color;

import l4g.Classroom;
import l4g.Grader;
import l4g.Presenter_Mode3;
import l4g.common.Classroom_Settings;
import l4g.common.GameNumberManager;
import l4g.customplayers.*;
import loot.GameFrameSettings;


public class Program
{
	public static void main(String[] args)
	{
		//settings�� ���ǽ� ����(���� �� �� ����)�� ���� ���� �ʵ���� ������ �ֽ��ϴ�.
		Classroom_Settings settings = new Classroom_Settings();
		
		/*
		 * mode���� �ٲ����ν� ������ ���� ���� ������� ������ �� �� �ֽ��ϴ�.
		 * 
		 * mode����	0�̸� �ܼ� â�� ����Ͽ� ���� �� ���� �����ϸ�
		 * 			�� �ϸ��� ù �÷��̾�(�Ƹ��� �������� ���� �� �÷��̾�)�� ���� �ǻ� ������ ����,
		 * 			� �ൿ�� �ߴ���, �þ� �������� � ��ǵ��� �߻��ߴ����� ����ϰ�,
		 *          �� ����� ù �÷��̾��� ���� ���� �� ������ ����� ����
		 *			���� Ű�� �Է¹��� ������ ������ �Ͻ� �����մϴ�. 
		 * 			������ ������ ù �÷��̾��� ������ ����մϴ�.
		 * 
		 * mode���� 1�̸� �ܼ� â�� ����Ͽ� ���� �� ���� �����ϸ� 
		 * 			�� �ϸ��� ��� �ൿ, ��� ������ ����ϰ�, 
		 * 			�� ����� ��� �÷��̾��� ���� ���� �� ������ ����� ���� 
		 *			���� Ű�� �Է¹��� ������ ������ �Ͻ� �����մϴ�. 
		 * 			������ ������ ��� �÷��̾��� ������ ����մϴ�.
		 * 
		 * mode���� 2�� �ܼ� â�� ����Ͽ� ������ ���� ��ȣ���� ���� ���� 10,000���� �����ϸ�
		 * 			������ ��� ������ �� �ι��� ���� �� ����� ����� ����
		 * 			ù �÷��̾�(�Ƹ��� �������� ���� �� �÷��̾�)�� ����� ����մϴ�.
		 * 
		 * mode���� 3�̸� �׽�Ʈ�� Presenter�� ����Ͽ� ���� �� ���� �����ϸ�
		 *			�̸� ���� ù �÷��̾�(�Ƹ��� �������� ���� �� �÷��̾�)�� ȹ���� �� �ִ� �������� �� �ϸ��� �ð������� ���� �� �� �ֽ��ϴ�.
		 *			������ ������ �ܼ� â�� ù �÷��̾��� ������ ����մϴ�.
		 *			������ ���� ���Ŀ��� ���� Ű�� ġ�� �� �ٽ� ������ �� �ֽ��ϴ�.
		 *
		 * ���ο� mode�� ���� ������ ����ʿ� ���� �߰��� �����˴ϴ�.
		 */
		
		int mode = 3;
		
		/*
		 * ���� ���� �÷��̾ ����ϴ� �κ��Դϴ�.
		 * �ڽ��� ���� �÷��̾� Ŭ������ �Ʒ��� �ּ� ������ �����Ͽ� ����ϼ���.
		 * (�ּ� ������ ������ ���� Player_YOURNAMEHERE ��� �������� �÷��̾� Ŭ���� �̸��� ������ �˴ϴ�)
		 * 
		 * ����: ���� Ŭ������ ���� �� ����ϴ� �͵� �����մϴ�.
		 * 		 ������ �̷��� �ϴ� ��� ���� �÷��̾ �׻� ���� �ǻ� ������ �ϰ� �� �� ������
		 * 		 ������ �� Ŭ������ �� ���� ����ϴ� ���� ���ڽ��ϴ�.
		 */
		
		// settings.custom_player_classes.add(Player_YOURNAMEHERE.class);
		settings.custom_player_classes.add(DEJAVA.class);
		
		/*
		 * ���� ���ӿ��� NPC(���� ������ ������ ���� �ʴ� �÷��̾�) ���� �����ϴ� �κ��Դϴ�.
		 * �������� �÷��̾ �׽�Ʈ�� �� �� �ʵ��� ���� �׳� 0���� �μ���. 
		 */		
		settings.numberOfNPCs = 0;

		/*
		 * �Ʒ��� ���� ��ȣ�� -1�� �ƴ� �ٸ� ������ �����ϸ�
		 * mode���� 0, 1, 3�� ��
		 * �׻� �ش� ���� ��ȣ�� ����Ͽ� ������ �����մϴ�.
		 * ����: ���� ��ȣ�� long �����̹Ƿ� ����� �ڿ� L�� �� �ٿ� �ּ���.
		 */
		settings.game_number = -1L;
		
		/*
		 * ���ӿ� ������ '������ Bot �÷��̾� ��'�� �ִ밪�� ������ �� �ֽ��ϴ�.
		 * 0 ���Ϸ� �θ� �����ڴ� ���ӿ� �������� �ʽ��ϴ�.
		 * 
		 * '���� ����'���� �� ���� 0���� �����˴ϴ�.  
		 */
		settings.max_numberOfHornDonePlayer = 0;
		
		/*
		 * ���ӿ� ������ Bot �÷��̾���� ������ ������ ���� �� ����ϴ�
		 * seed ���ڿ��� �����ϴ� �κ��Դϴ�.
		 * 
		 * �⺻���� "16OODP"�̸�
		 * �̸� �ٸ� ���ڿ��� �ٲٴ� ��� ���ӿ� ���� ������ Bot �÷��̾� ��ϵ� �ٲ�� �˴ϴ�.
		 * 
		 * �ڽ��� �÷��̾ ���� ���� �÷��̾� ���� �ȿ��� �׽�Ʈ�� ������ ���
		 * "16OODP" ��� �ٸ� ���ڿ��� ������ ������.
		 * 
		 * ����: ���� �� ���ڿ��� '�� ���ڿ�' �Ǵ� null�� �����ϴ� ���
		 * 		 ���ǽ��� �ʱ�ȭ�� �� �ٽ� �⺻������ �����˴ϴ�.
		 */
		settings.seed_for_sample_players = "16OODP";		
		
		/*
		 * �Ʒ� �������� true�� �θ�
		 * ���� ���� ���� �÷��̾ ��Ÿ�� ���ܸ� ���� ���
		 * �ش� ���ܿ� ���� ������ �ܼ� â�� ����մϴ�.
		 * 
		 * �̰� �̿ܿ��� settings���� �پ��� ���� �ɼǵ��� ��� ������
		 * �Ʒ��� �� mode�� ���� �κ��� ������ �����Ͽ�
		 * ������ �Ը��� �´� �׽�Ʈ�� ������ �� �� �ֽ��ϴ�.
		 */
		settings.print_errors = true;
		
		
		
		
		
		/* --------------------------------------------------------------------------------------------
		 * 
		 * �� �Ʒ����� �� ��庰 ���� �ڵ尡 �����Ǿ� �ֽ��ϴ�.
		 * �׽�Ʈ ȯ���� �� �Ը���� �����ϰ� ���� �� �ƴ϶��
		 * �� �Ʒ��� �ִ� ������ �� ���� �˴ϴ�.
		 * 
		 */

		if ( mode == 0 )
		{
			settings.print_first_player_only = true;
			settings.print_decisions = true;
			settings.print_actions = true;
			settings.print_reactions = true;
			settings.print_playerInfos = true;
			settings.print_scores_at_each_turns = true;
			settings.print_scores_at_the_end = true;
			
			settings.callback_EndTurn = () ->
			{
				try
				{
					System.out.print("Press Enter to continue...");
					System.in.skip(System.in.available());
					System.in.read();
				}
				catch ( Exception e )
				{
					
				}
				return true;
			};
			
			Classroom classroom = new Classroom(settings);
			classroom.Initialize();
			classroom.Start();
		}
		
		if ( mode == 1 )
		{
			settings.print_first_player_only = false;
			settings.print_decisions = false;
			settings.print_actions = true;
			settings.print_reactions = true;
			settings.print_playerInfos = true;
			settings.print_scores_at_each_turns = true;
			settings.print_scores_at_the_end = true;
			
			settings.callback_EndTurn = () ->
			{
				try
				{
					System.out.print("Press Enter to continue...");
					System.in.skip(System.in.available());
					System.in.read();
				}
				catch ( Exception e )
				{
					
				}
				return true;
			};
			
			Classroom classroom = new Classroom(settings);
			classroom.Initialize();
			classroom.Start();
		}
		
		if ( mode == 2 )
		{
			GameNumberManager numbers = new GameNumberManager(10000);
			numbers.Create(-1);
			
			Grader grader = new Grader();
			
			settings.use_console_mode = false;

			Classroom classroom = null;
			
			for ( int iGame = 0; iGame < 10000; ++iGame )
			{
				if ( iGame % 1000 == 0 )
					System.out.println(iGame + " / 10000 games completed...");
				settings.game_number = numbers.data[iGame];
				classroom = new Classroom(settings);
				classroom.Initialize();
				classroom.Start();
				grader.Update(classroom);
			}

			System.out.println("Done.");
			
			grader.PrintResults(classroom);
			grader.PrintResultsOf(0, classroom);
		}
		
		if ( mode == 3 )
		{
			settings.print_first_player_only = true;
			settings.print_decisions = false;
			settings.print_actions = false;
			settings.print_reactions = false;
			settings.print_playerInfos = false;
			settings.print_scores_at_each_turns = false;
			settings.print_scores_at_the_end = true;
			
			GameFrameSettings gfs = new GameFrameSettings();
			gfs.window_title = "L4G2EP2-100ME �׽�Ʈ�� Presenter";
			gfs.canvas_width = 1080;
			gfs.canvas_height = 850;
			gfs.numberOfButtons = 20;
			gfs.canvas_backgroundColor = Color.lightGray;
			
			Presenter_Mode3 window = new Presenter_Mode3(gfs, settings);
			window.setVisible(true);
		}
	}
}
