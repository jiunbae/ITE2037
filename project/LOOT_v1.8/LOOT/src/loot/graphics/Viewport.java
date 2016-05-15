package loot.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * 3차원 내부 좌표계를 만들고 이를 통해 내부 요소들을 자신의 view 영역에 그리는 클래스입니다.<br>
 * 참고: Viewport는 위치 계산 결과 자신의 영역 안에 걸쳐 있지 않은 요소들은 아예 그리지 않으며<br>
 * 항상 자신의 영역 안에서만 그리기 작업을 수행합니다.
 * 
 * @author Racin
 *
 */
public class Viewport extends Layer
{
	public double pointOfView_x;
	public double pointOfView_y;
	public double pointOfView_z;
	
	public double view_baseDistance;
	public double view_minDistance;
	public double view_maxDistance;
	
	public double view_origin_3D_x;
	public double view_origin_3D_y;
	public double view_exponent_z;
	
	
	public ArrayList<VisualObject3D> children_3d_sorted = new ArrayList<VisualObject3D>();

	public ArrayList<VisualObject> children_2d = new ArrayList<>();

	public Viewport(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		pointOfView_x = 0;
		pointOfView_y = 0;
		pointOfView_z = 1;
		
		view_baseDistance = 1;
		view_minDistance = 0;
		view_maxDistance = Double.POSITIVE_INFINITY;
		
		view_origin_3D_x = 0.5;
		view_origin_3D_y = 0.5;
		view_exponent_z = 1;
	}
	
	public Viewport(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		
		pointOfView_x = 0;
		pointOfView_y = 0;
		pointOfView_z = 1;
		
		view_baseDistance = 1;
		view_minDistance = 0;
		view_maxDistance = Double.POSITIVE_INFINITY;
		
		view_origin_3D_x = 0.5;
		view_origin_3D_y = 0.5;
		view_exponent_z = 1;
	}

	/**
	 * 현재 Viewport에 지정된 영역에 내부 요소들을 그립니다.<br>
	 * 참고 1: Viewport는 위치 계산 결과 자신의 영역 안에 걸쳐 있지 않은 요소들은 아예 그리지 않으며<br>
	 * 항상 자신의 영역 안에서만 그리기 작업을 수행합니다.<br>
	 * 참고 2: Viewport는 먼저 3차원 요소들을 그린 다음 2차원 요소들을 그립니다.<br>
	 * 따라서 2차원 요소들은 항상 영역 위에 있는 것처럼 보이게 됩니다.
	 * 
	 * @param g_origin
	 * 		GameFrame에는 g 라는 필드가 들어 있습니다.<br>
	 * 		여러분이 Draw(g)를 직접 호출할 때(게임 화면에 직접 그릴 때)는 더 고민하지 말고 그 필드를 그냥 넣으면 됩니다.
	 */
	@Override
	public void Draw(Graphics2D g_origin)
	{
		Graphics2D g = (Graphics2D)g_origin.create();
		
		g.clipRect(x, y, width, height);

		//변환 행렬 및 역행렬 작성
		transform_out.setToIdentity();
		transform_out.translate(x, y);
		transform_out.scale(width / view_width, height / view_height);
		transform_out.translate(view_width * view_origin_x, view_height * view_origin_y);

		transform_in.setToIdentity();
		transform_in.translate(-view_width * view_origin_x, -view_height * view_origin_y);
		transform_in.scale(view_width / width, view_height / height);	//'a/b배'의 역연산은 'b/a배' 
		transform_in.translate(-x, -y);									//'(a, b)만큼 이동'의 역연산은 '(-a, -b)만큼 이동'

		//그리기 작업을 위해 변환 행렬을 g에 적용
		g.transform(transform_out);
		
		children_3d_sorted.clear();
		children_2d.clear();
		
		for ( Iterator<VisualObject> iterator = children.iterator(); iterator.hasNext(); )
		{
			VisualObject child = iterator.next();
			
			if ( child.trigger_remove == true )
			{
				iterator.remove();
				continue;
			}
			
			if ( child.trigger_hide == true )
				continue;
			
			if ( VisualObject3D.class.isInstance(child) == true )
			{
				VisualObject3D child_3d = (VisualObject3D)child;

				if ( child_3d.trigger_coordination == true )
				{
					double factor_z = pointOfView_z - child_3d.pos_z;
					
					if ( factor_z >= view_minDistance && factor_z <= view_maxDistance )
					{
						factor_z /= view_baseDistance;
						factor_z = Math.pow(factor_z, view_exponent_z);
							
						child_3d.x = (int)( view_width * view_origin_3D_x + ( child_3d.pos_x - pointOfView_x - child_3d.radius_x ) / factor_z );
						child_3d.y = (int)( view_height * view_origin_3D_y - ( child_3d.pos_y - pointOfView_y + child_3d.radius_y ) / factor_z );
						child_3d.width = (int)( child_3d.radius_x / factor_z * 2 );
						child_3d.height = (int)( child_3d.radius_y / factor_z * 2 );
						
						children_3d_sorted.add(child_3d);
					}
					
					continue;
				}
			}

			children_2d.add(child);
		}

		SortChildren();
		
		for ( ListIterator<VisualObject3D> iterator_3d = children_3d_sorted.listIterator(children_3d_sorted.size()); iterator_3d.hasPrevious() == true; )
			iterator_3d.previous().Draw(g);
		
		for ( ListIterator<VisualObject> iterator_2d = children_2d.listIterator(children_2d.size()); iterator_2d.hasPrevious() == true;  )
			iterator_2d.previous().Draw(g);
		
		g.dispose();		
	}
	
	/**
	 * Viewport.Draw(g) 안에서 3차원 요소의 그리기 순서를 결정하기 위해 children_3d_sorted 목록을 정렬하는 메서드입니다.<br>
	 * 이 메서드는 Viewport 내부에서 자동으로 호출되며 여러분은 이 메서드를 사용할 수 없습니다.
	 */
	private void SortChildren()
	{
		int length = children_3d_sorted.size();
		
		if ( length < 2 )
			return;
		
		ArrayList<VisualObject3D> buffer = new ArrayList<>(length);
		
		for ( int iChild = 0; iChild < length; ++iChild )
			buffer.add(null);
		
		for ( int block_size = 1; block_size <= length; block_size *= 2 )
		{
			int pos_buffer = 0;
			
			for ( int start_offset = 0; start_offset < length; start_offset += block_size * 2 )
			{
				int middle_offset = start_offset + block_size;
				
				if ( middle_offset > length )
					middle_offset = length;
				
				int end_offset = middle_offset + block_size;
				
				if ( end_offset > length )
					end_offset = length;
				
				int pos_left = start_offset;
				int pos_right = middle_offset;
				
				while ( pos_left < middle_offset && pos_right < end_offset )
				{
					VisualObject3D child_left = children_3d_sorted.get(pos_left);
					VisualObject3D child_right = children_3d_sorted.get(pos_right);
					
					if ( child_left.pos_z >= child_right.pos_z )
					{
						buffer.set(pos_buffer, child_left);
						++pos_buffer;
						++pos_left;
					}
					else
					{
						buffer.set(pos_buffer, child_right);
						++pos_buffer;
						++pos_right;
					}
				}
				
				while ( pos_left < middle_offset )
				{
					buffer.set(pos_buffer, children_3d_sorted.get(pos_left));
					++pos_buffer;
					++pos_left;					
				}
				
				while ( pos_right < end_offset )
				{
					buffer.set(pos_buffer, children_3d_sorted.get(pos_right));
					++pos_buffer;
					++pos_right;
				}
			}
			
			ArrayList<VisualObject3D> temp = children_3d_sorted;
			children_3d_sorted = buffer;
			buffer = temp;
		}
	}
	
	
	/* ---------------------------------------------------
	 * 
	 * 적중 테스트를 위한 메서드들
	 * 
	 */

	/**
	 * 현재 Viewport의 view 평면에 해당 3차원 좌표가 보이는지 여부를 반환합니다. 
	 * 
	 * @param pos_x_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 */
	public boolean CheckVisibilityOfPosition3D(double pos_x_internal, double pos_y_internal, double pos_z_internal)
	{
		double factor_z = pointOfView_z - pos_z_internal;
		
		if ( factor_z >= view_minDistance && factor_z <= view_maxDistance )
		{
			factor_z /= view_baseDistance;
			
			int x_internal = (int)( view_width / 2 + ( pos_x_internal - pointOfView_x ) / factor_z );
			int y_internal = (int)( view_height / 2 - ( pos_y_internal - pointOfView_y ) / factor_z );
			
			return	x_internal >= 0 && x_internal <= width &&
					y_internal >= 0 && y_internal <= height;
		}
		
		return false;
	}

	/**
	 * 현재 Viewport의 view 평면에 해당 3차원 좌표가 보이는지 여부를 반환합니다. 
	 * 
	 * @param pos_x_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 가시성 여부를 반환할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 * @param radius_z 가시성 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	public boolean CheckVisibilityOfPosition3D(double pos_x_internal, double pos_y_internal, double pos_z_internal, double radius_z)
	{
		double factor_z = pointOfView_z - pos_z_internal;
		
		if ( factor_z + radius_z >= view_minDistance && factor_z - radius_z <= view_maxDistance )
		{
			factor_z /= view_baseDistance;
			
			int x_internal = (int)( view_width / 2 + ( pos_x_internal - pointOfView_x ) / factor_z );
			int y_internal = (int)( view_height / 2 - ( pos_y_internal - pointOfView_y ) / factor_z );
			
			return	x_internal >= 0 && x_internal <= width &&
					y_internal >= 0 && y_internal <= height;
		}
		
		return false;
	}
	
	/**
	 * 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.
	 * 
	 * @param x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */	
	@Override
	public boolean HitTest(int x, int y)
	{
		if ( HitTest_Base(x, y) == true )
		{
			Point pos_internal = TransformToInternalPosition(x, y);
			
			for ( VisualObject child_2d : children_2d )
				if ( child_2d.trigger_hide == false && child_2d.trigger_ignoreDuringHitTest == false && child_2d.HitTest(pos_internal.x, pos_internal.y) == true )
					return true;
			
			for ( VisualObject3D child_3d : children_3d_sorted )
				if ( child_3d.trigger_hide == false && child_3d.trigger_ignoreDuringHitTest == false && child_3d.HitTest(pos_internal.x, pos_internal.y) == true )
					return true;
		}
			
		return false;
	}
	
	/**
	 * <b>Viewport 내부의</b> 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: double 형식의 특성상 이 메서드는 radius_z를 포함한 버전을 사용하는 것이 더 용이합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 일반적인 메서드들과 다르게 Viewport.HitTest3D()는 Viewport가 속한 외부 좌표계가 아닌 Viewport 자신이 다루는 내부 좌표계의 좌표를 argument로 받습니다. 
	 * 따라서 필요한 경우 Viewport.TransformToInternal3DPosition()을 호출하여 외부 2차원 좌표를 내부 3차원 좌표로 변환한 다음 이 메서드를 호출해야 합니다. 
	 * 다만, 만약 Viewport 안에 다른 Viewport가 들어 있는 경우에는 이 과정을 자동으로 수행해 주므로 
	 * 여러분이 이러한 제약을 직접 느끼게 될 일은 아마도 별로 발생하지 않을 것입니다. 
	 * 
	 * @param pos_x_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 */
	@Override
	public boolean HitTest3D(double pos_x_internal, double pos_y_internal, double pos_z_internal)
	{
		if ( CheckVisibilityOfPosition3D(pos_x_internal, pos_y_internal, pos_z_internal) == true )
		{
			for ( VisualObject3D child : children_3d_sorted )
			{
				//만약 해당 내부 요소가 Viewport인 경우 추가 좌표 변환을 통해 내부 Viewport에 대한 내부 3차원 좌표로 변환하여 HitTest3D() 호출
				if ( Viewport.class.isInstance(child) == true )
				{
					Viewport child_viewport = (Viewport)child;
					Point pos_internal_2d = TransformToInternal2DPositionFromInternal3D(pos_x_internal, pos_y_internal, pos_z_internal);
					Point3D pos_inChild_3d = child_viewport.TransformToInternal3DPosition(pos_internal_2d.x, pos_internal_2d.y);
					
					if ( child_viewport.trigger_hide == false && child_viewport.trigger_ignoreDuringHitTest == false && child_viewport.HitTest3D(pos_inChild_3d.x, pos_inChild_3d.y, pos_inChild_3d.z) == true )
						return true;
				}
				//그렇지 않은 경우 현재 3차원 내부 좌표를 그대로 사용하여 내부 요소의 HitTest3D() 호출
				else
				{
					if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest3D(pos_x_internal, pos_y_internal, pos_z_internal) == true )
						return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * <b>Viewport 내부의</b> 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: LOOT에서 모든 요소는 실질적으로 2차원 요소이므로<br>
	 * 여기서는 적중 테스트를 용이하게 하기 위해 z축 방향 반지름을 적용하며<br>
	 * 요소의 실제 z좌표와 입력받은 z좌표 사이의 거리가 해당 반지름보다 작거나 같은 경우 적중한 것으로 간주합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 일반적인 메서드들과 다르게 Viewport.HitTest3D()는 Viewport가 속한 외부 좌표계가 아닌 Viewport 자신이 다루는 내부 좌표계의 좌표를 argument로 받습니다. 
	 * 따라서 필요한 경우 Viewport.TransformToInternal3DPosition()을 호출하여 외부 2차원 좌표를 내부 3차원 좌표로 변환한 다음 이 메서드를 호출해야 합니다. 
	 * 다만, 만약 Viewport 안에 다른 Viewport가 들어 있는 경우에는 이 과정을 자동으로 수행해 주므로 
	 * 여러분이 이러한 제약을 직접 느끼게 될 일은 아마도 별로 발생하지 않을 것입니다. 
	 * 
	 * @param pos_x_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	@Override
	public boolean HitTest3D(double pos_x_internal, double pos_y_internal, double pos_z_internal, double radius_z)
	{
		if ( CheckVisibilityOfPosition3D(pos_x_internal, pos_y_internal, pos_z_internal, radius_z) == true )
		{
			for ( VisualObject3D child : children_3d_sorted )
			{
				//만약 해당 내부 요소가 Viewport인 경우 추가 좌표 변환을 통해 내부 Viewport에 대한 내부 3차원 좌표로 변환하여 HitTest3D() 호출
				if ( Viewport.class.isInstance(child) == true )
				{
					Viewport child_viewport = (Viewport)child;
					Point pos_internal_2d = TransformToInternal2DPositionFromInternal3D(pos_x_internal, pos_y_internal, pos_z_internal);
					Point3D pos_inChild_3d = child_viewport.TransformToInternal3DPosition(pos_internal_2d.x, pos_internal_2d.y);
					
					if ( child_viewport.trigger_hide == false && child_viewport.trigger_ignoreDuringHitTest == false && child_viewport.HitTest3D(pos_inChild_3d.x, pos_inChild_3d.y, pos_inChild_3d.z, radius_z) == true )
						return true;
				}
				//그렇지 않은 경우 현재 3차원 내부 좌표를 그대로 사용하여 내부 요소의 HitTest3D() 호출
				else
				{
					if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest3D(pos_x_internal, pos_y_internal, pos_z_internal, radius_z) == true )
						return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * 해당 좌표 위에 있는 내부 요소를 반환합니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우 null을 반환합니다.<br>
	 * 만약 해당 좌표 위에 두 개 이상의 요소가 있는 경우 가장 나중에 그려진(실제 게임 화면에 보이는) 요소를 반환합니다.<br>
	 * 
	 * @param x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	@Override
	public VisualObject GetObjectAt(int x, int y)
	{
		if ( HitTest_Base(x, y) == true )
		{
			Point pos_internal = TransformToInternalPosition(x, y);
			
			for ( VisualObject child_2d : children_2d )
				if ( child_2d.trigger_hide == false && child_2d.trigger_ignoreDuringHitTest == false && child_2d.HitTest(pos_internal.x, pos_internal.y) == true )
					return child_2d;
			
			for ( VisualObject3D child_3d : children_3d_sorted )
				if ( child_3d.trigger_hide == false && child_3d.trigger_ignoreDuringHitTest == false && child_3d.HitTest(pos_internal.x, pos_internal.y) == true )
					return child_3d;
		}
			
		return null;
	}
	
	/**
	 * <b>Viewport 내부의</b> 3차원 공간 안에서, 해당 좌표 위에 있는 내부 요소를 반환합니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우 null을 반환합니다.<br>
	 * 만약 해당 좌표 위에 두 개 이상의 요소가 있는 경우 가장 나중에 그려진(실제 게임 화면에 보이는) 요소를 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 일반적인 메서드들과 다르게 Viewport.GetObjectAt3D()는 Viewport가 속한 외부 좌표계가 아닌 Viewport 자신이 다루는 내부 좌표계의 좌표를 argument로 받습니다. 
	 * 따라서 필요한 경우 Viewport.TransformToInternal3DPosition()을 호출하여 외부 2차원 좌표를 내부 3차원 좌표로 변환한 다음 이 메서드를 호출해야 합니다. 
	 * 다만, 만약 Viewport 안에 다른 Viewport가 들어 있는 경우에는 이 과정을 자동으로 수행해 주므로 
	 * 여러분이 이러한 제약을 직접 느끼게 될 일은 아마도 별로 발생하지 않을 것입니다.
	 * 
	 * @param pos_x_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 */
	public VisualObject3D GetObjectAt3D(double pos_x_internal, double pos_y_internal, double pos_z_internal)
	{
		if ( CheckVisibilityOfPosition3D(pos_x_internal, pos_y_internal, pos_z_internal) == true )
		{
			for ( VisualObject3D child : children_3d_sorted )
			{
				//만약 해당 내부 요소가 Viewport인 경우 추가 좌표 변환을 통해 내부 Viewport에 대한 내부 3차원 좌표로 변환하여 HitTest3D() 호출
				if ( Viewport.class.isInstance(child) == true )
				{
					Viewport child_viewport = (Viewport)child;
					Point pos_internal_2d = TransformToInternal2DPositionFromInternal3D(pos_x_internal, pos_y_internal, pos_z_internal);
					Point3D pos_inChild_3d = child_viewport.TransformToInternal3DPosition(pos_internal_2d.x, pos_internal_2d.y);
					
					if ( child_viewport.trigger_hide == false && child_viewport.trigger_ignoreDuringHitTest == false && child_viewport.HitTest3D(pos_inChild_3d.x, pos_inChild_3d.y, pos_inChild_3d.z) == true )
						return child;
				}
				//그렇지 않은 경우 현재 3차원 내부 좌표를 그대로 사용하여 내부 요소의 HitTest3D() 호출
				else
				{
					if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest3D(pos_x_internal, pos_y_internal, pos_z_internal) == true )
						return child;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * <b>Viewport 내부의</b> 3차원 공간 안에서, 해당 좌표 위에 있는 내부 요소를 반환합니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우 null을 반환합니다.<br>
	 * 만약 해당 좌표 위에 두 개 이상의 요소가 있는 경우 가장 나중에 그려진(실제 게임 화면에 보이는) 요소를 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 일반적인 메서드들과 다르게 Viewport.GetObjectAt3D()는 Viewport가 속한 외부 좌표계가 아닌 Viewport 자신이 다루는 내부 좌표계의 좌표를 argument로 받습니다. 
	 * 따라서 필요한 경우 Viewport.TransformToInternal3DPosition()을 호출하여 외부 2차원 좌표를 내부 3차원 좌표로 변환한 다음 이 메서드를 호출해야 합니다. 
	 * 다만, 만약 Viewport 안에 다른 Viewport가 들어 있는 경우에는 이 과정을 자동으로 수행해 주므로 
	 * 여러분이 이러한 제약을 직접 느끼게 될 일은 아마도 별로 발생하지 않을 것입니다.
	 * 
	 * @param pos_x_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 적중 여부를 검사할, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	public VisualObject3D GetObjectAt3D(double pos_x_internal, double pos_y_internal, double pos_z_internal, double radius_z)
	{
		if ( CheckVisibilityOfPosition3D(pos_x_internal, pos_y_internal, pos_z_internal, radius_z) == true )
		{
			for ( VisualObject3D child : children_3d_sorted )
			{
				//만약 해당 내부 요소가 Viewport인 경우 추가 좌표 변환을 통해 내부 Viewport에 대한 내부 3차원 좌표로 변환하여 HitTest3D() 호출
				if ( Viewport.class.isInstance(child) == true )
				{
					Viewport child_viewport = (Viewport)child;
					Point pos_internal_2d = TransformToInternal2DPositionFromInternal3D(pos_x_internal, pos_y_internal, pos_z_internal);
					Point3D pos_inChild_3d = child_viewport.TransformToInternal3DPosition(pos_internal_2d.x, pos_internal_2d.y);
					
					if ( child_viewport.trigger_hide == false && child_viewport.trigger_ignoreDuringHitTest == false && child_viewport.HitTest3D(pos_inChild_3d.x, pos_inChild_3d.y, pos_inChild_3d.z, radius_z) == true )
						return child;
				}
				//그렇지 않은 경우 현재 3차원 내부 좌표를 그대로 사용하여 내부 요소의 HitTest3D() 호출
				else
				{
					if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest3D(pos_x_internal, pos_y_internal, pos_z_internal) == true )
						return child;
				}
			}
		}
		
		return null;
	}
	
	
	/* ---------------------------------------------------
	 * 
	 * 상대좌표 계산 및 좌표 변환을 위한 메서드들
	 * 
	 */
	
	/**
	 * 해당 내부 요소를 기준으로 하는, Viewport가 속해 있는 2차원 평면 내 좌표에 대한, Viewport가 다루는 3차원 공간 내 상대좌표를 반환합니다.<br>
	 * 참고: 이 요소와 같은 평면 위에 있는 좌표를 대상으로 삼으므로 만들어진 상대좌표의 pox_z 값은 항상 0이 됩니다.
	 * 
	 * @param origin 3차원 상대좌표를 계산할 원점이 되는 요소입니다.
	 * @param x 3차원 상대좌표를 반환하려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 x좌표입니다.
	 * @param y 3차원 상대좌표를 반환하려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 y좌표입니다.
	 */	
	public Point3D GetRelativePosition3DFrom(VisualObject3D origin, int x, int y)
	{
		//해당 요소가 Viewport의 내부 요소가 아닌 경우 실패
		if ( children_3d_sorted.contains(origin) == false )
			return null;
		
		Point pos_internal = TransformToInternalPosition(x, y);
		
		//해당 요소가 Viewport의 내부 요소인 경우 그냥 해당 요소에 대해 VisualObject3D.GetRelativePosition3D() 직접 호출
		//--> 3D to 2D 좌표 변환은 각 요소 혼자서도 사실 잘 할 수 있음
		return origin.GetRelativePosition3D(pos_internal.x, pos_internal.y);
	}

	/**
	 * 해당 내부 요소를 기준으로 하는, <b>Viewport 내부의</b> 3차원 공간 내 좌표에 대한, Viewport가 다루는 2차원 평면 내 상대좌표를 반환합니다.<br>
	 * 이는 곧, 2차원 view 평면에 3차원 요소들을 배치하듯 실질적으로 2차원 개념인 해당 내부 요소를 view 평면으로 삼아 주어진 좌표를 project하여 배치한 2차원 좌표를 계산함을 의미합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 일반적인 메서드들과 다르게 Viewport.GetRelativePositionFrom()은 Viewport가 속한 외부 좌표계가 아닌 Viewport 자신이 다루는 내부 좌표계의 좌표를 argument로 받습니다.
	 * 따라서 필요한 경우 Viewport.TransformToInternal3DPosition()을 호출하여 외부 2차원 좌표를 내부 3차원 좌표로 변환한 다음 이 메서드를 호출해야 합니다.
	 * 
	 * @param origin 2차원 상대좌표를 계산할 원점이 되는 요소입니다.
	 * @param pos_x_internal 2차원 상대좌표를 반환하려 하는, 이 Viewport가 다루는 내부 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 2차원 상대좌표를 반환하려 하는, 이 Viewport가 다루는 내부 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 2차원 상대좌표를 반환하려 하는, 이 Viewport가 다루는 내부 좌표계 안에서의 z좌표입니다.
	 */
	public Point GetRelativePositionFrom(VisualObject3D origin, double pos_x_internal, double pos_y_internal, double pos_z_internal)
	{
		//해당 요소가 Viewport의 내부 요소가 아닌 경우 실패
		if ( children_3d_sorted.contains(origin) == false )
			return null;
		
		double factor_z = ( origin.pos_z - pos_z_internal ) / view_baseDistance;
		
		//Viewport.Draw(g)와 달리 여기선 factor_z가 음수일 수 있으므로(대상 좌표가 내부 요소보다 더 '멀리' 있는 경우) 절대값을 계산하여 사용
		if ( factor_z < 0 )
			factor_z = -factor_z;
		
		double pos_x_projected = ( pos_x_internal - origin.pos_x ) / factor_z + origin.pos_x;
		double pos_y_projected = ( pos_y_internal - origin.pos_y ) / factor_z + origin.pos_y;
		
		//마지막으로, project된 3차원 좌표를 2차원 좌표로 변환하여 반환
		return TransformToInternal2DPositionFromInternal3D(pos_x_projected, pos_y_projected, origin.pos_z);
	}

	/**
	 * Viewport가 속해 있는 2차원 평면 내의 좌표에 대한, Viewport가 다루는 3차원 공간 내 좌표를 만들어 반환합니다.<br>
	 * 만약 해당 좌표 위에 내부 요소들 중 하나 이상이 있는 경우 새 좌표는 해당 요소와 같은 pos_z값을 가지게 됩니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우(해당 좌표가 view 평면 영역을 벗어난 경우 포함), 또는 해당 좌표 위에 2차원 요소가 놓여 있는 경우 null을 반환합니다.
	 * 
	 * @param x 3차원 좌표를 만들려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 x좌표입니다.
	 * @param y 3차원 좌표를 만들려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 y좌표입니다.
	 */
	public Point3D TransformToInternal3DPosition(int x, int y)
	{
		//해당 좌표 위에 어떤 내부 요소가 놓여 있는지 먼저 탐색
		VisualObject target = GetObjectAt(x, y);
		
		//해당 좌표 위에 아무 요소도 없거나 있긴 한데 2차원 요소인 경우 실패
		if ( target == null || VisualObject3D.class.isInstance(target) == false )
			return null;
		
		VisualObject3D target3D = (VisualObject3D)target;
		
		//해당 좌표 위에 3차원 요소가 놓여 있지만 현재 2차원 모드로 운용중인 경우 역시 실패
		if ( target3D.trigger_coordination == false )
			return null;
		
		//외부 좌표를 내부 좌표로 변환
		Point pos_internal = TransformToInternalPosition(x, y);
		
		//내부 좌표를 통해 해당 요소와 같은 pos_z값을 갖는 3차원 좌표를 만들어 반환 
		return target3D.TransformTo3DPosition(pos_internal.x, pos_internal.y);
	}
	
	/**
	 * Viewport가 다루는 2차원 평면 내의 좌표에 대한, Viewport가 다루는 3차원 공간 내 좌표를 만들어 반환합니다.<br>
	 * 만약 해당 좌표 위에 내부 요소들 중 하나 이상이 있는 경우 새 좌표는 해당 요소와 같은 pos_z값을 가지게 됩니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우(해당 좌표가 view 평면 영역을 벗어난 경우 포함), 또는 해당 좌표 위에 2차원 요소가 놓여 있는 경우 null을 반환합니다.
	 * 
	 * @param x_internal 3차원 좌표를 만들려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 x좌표입니다.
	 * @param y_internal 3차원 좌표를 만들려 하는, Viewport가 속해 있는 2차원 좌표계 안에서의 y좌표입니다.
	 */
	public Point3D TransformToInternal3DPositionFromInternal2D(int x_internal, int y_internal)
	{
		//해당 좌표 위에 어떤 내부 요소가 놓여 있는지 먼저 탐색
		VisualObject target = GetObjectAt(TransformToExternalPosition(x_internal, y_internal));
		
		//해당 좌표 위에 아무 요소도 없거나 있긴 한데 2차원 요소인 경우 실패
		if ( target == null || VisualObject3D.class.isInstance(target) == false )
			return null;
		
		VisualObject3D target3D = (VisualObject3D)target;
		
		//해당 좌표 위에 3차원 요소가 놓여 있지만 현재 2차원 모드로 운용중인 경우 역시 실패
		if ( target3D.trigger_coordination == false )
			return null;
		
		//내부 좌표를 통해 해당 요소와 같은 pos_z값을 갖는 3차원 좌표를 만들어 반환 
		return target3D.TransformTo3DPosition(x_internal, y_internal);
	}
	
	/**
	 * Viewport가 다루는 3차원 공간 안의 좌표에 대한, Viewport가 다루는 2차원 평면 내 좌표를 만들어 반환합니다.<br>
	 * 만약 Viewport를 통해 해당 좌표를 볼 수 없는 경우 null을 반환합니다.
	 * 
	 * @param pos_x_internal 2차원 좌표를 반환하려 하는, 이 Viewport가 다루는 3차원 좌표계 안에서의 x좌표입니다.
	 * @param pos_y_internal 2차원 좌표를 반환하려 하는, 이 Viewport가 다루는 3차원 좌표계 안에서의 y좌표입니다.
	 * @param pos_z_internal 2차원 좌표를 반환하려 하는, 이 Viewport가 다루는 3차원 좌표계 안에서의 z좌표입니다.
	 */
	public Point TransformToInternal2DPositionFromInternal3D(double pos_x_internal, double pos_y_internal, double pos_z_internal)
	{
		double factor_z = pointOfView_z - pos_z_internal;
		
		if ( factor_z >= view_minDistance && factor_z <= view_maxDistance )
		{
			factor_z /= view_baseDistance;
			
			int x_internal = (int)( view_width / 2 + ( pos_x_internal - pointOfView_x ) / factor_z );
			int y_internal = (int)( view_height / 2 - ( pos_y_internal - pointOfView_y ) / factor_z );
			
			if ( x_internal >= 0 && x_internal <= width && y_internal >= 0 && y_internal <= height == false )
			{
				return new Point(x_internal, y_internal);
			}
		}
		
		return null;
	}
}
