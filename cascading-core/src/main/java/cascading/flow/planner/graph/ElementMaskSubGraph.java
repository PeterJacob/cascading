/*
 * Copyright (c) 2007-2014 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.flow.planner.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cascading.flow.FlowElement;
import cascading.flow.planner.Scope;
import cascading.util.Util;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMaskSubgraph;
import org.jgrapht.graph.MaskFunctor;

/**
 *
 */
public class ElementMaskSubGraph extends DirectedMaskSubgraph<FlowElement, Scope> implements ElementGraph
  {
  private DirectedGraph<FlowElement, Scope> elementGraph;
  private FlowElementMaskFunctor mask;

  private static class FlowElementMaskFunctor implements MaskFunctor<FlowElement, Scope>
    {
    Set<FlowElement> maskedElements = new HashSet<>();
    Set<Scope> maskedScopes = new HashSet<>();

    public FlowElementMaskFunctor( Collection<FlowElement> flowElements, Collection<Scope> scopes )
      {
      this( flowElements );

      if( scopes != null )
        maskedScopes.addAll( scopes );
      }

    public FlowElementMaskFunctor( Collection<FlowElement> flowElements )
      {
      if( flowElements != null )
        maskedElements.addAll( flowElements );
      }

    @Override
    public boolean isEdgeMasked( Scope scope )
      {
      return maskedScopes.contains( scope );
      }

    @Override
    public boolean isVertexMasked( FlowElement flowElement )
      {
      return maskedElements.contains( flowElement );
      }
    }

  public ElementMaskSubGraph( DirectedGraph<FlowElement, Scope> elementGraph, FlowElement... maskedFlowElements )
    {
    this( elementGraph, new FlowElementMaskFunctor( Arrays.asList( maskedFlowElements ) ) );
    }

  public ElementMaskSubGraph( DirectedGraph<FlowElement, Scope> elementGraph, Collection<FlowElement> maskedFlowElements )
    {
    this( elementGraph, new FlowElementMaskFunctor( maskedFlowElements ) );
    }

  public ElementMaskSubGraph( DirectedGraph<FlowElement, Scope> elementGraph, Collection<FlowElement> maskedFlowElements, Collection<Scope> maskedScopes )
    {
    this( elementGraph, new FlowElementMaskFunctor( maskedFlowElements, maskedScopes ) );
    }

  public ElementMaskSubGraph( ElementMaskSubGraph graph )
    {
    this( graph.elementGraph, graph.mask );
    }

  protected ElementMaskSubGraph( DirectedGraph<FlowElement, Scope> elementGraph, FlowElementMaskFunctor flowElementMaskFunctor )
    {
    super( elementGraph, flowElementMaskFunctor );

    this.elementGraph = elementGraph;
    this.mask = flowElementMaskFunctor;
    }

  @Override
  public ElementGraph copyGraph()
    {
    return new ElementMaskSubGraph( this );
    }

  @Override
  public void writeDOT( String filename )
    {
    boolean success = ElementGraphs.printElementGraph( filename, this, null );

    if( success )
      Util.writePDF( filename );
    }

  @Override
  public boolean equals( Object object )
    {
    return ElementGraphs.equals( this, (Graph) object );
    }

  @Override
  public int hashCode()
    {
    int result = super.hashCode();
    result = 31 * result; // parity with AnnotatedGraph types
    return result;
    }
  }
