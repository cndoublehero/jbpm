/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples.request;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RequestUI extends JFrame {

    private static final long serialVersionUID = 510l;
    
    private int requestId = 0;
    private JTextField nameField;
    private JTextField amountField;
    private JTextField signalField;
    private JTextField processField;
    private StatefulKnowledgeSession ksession;
    private WorkflowProcessInstance processInstance;
    
    public static void main(String[] args) {
    	new RequestUI().setVisible(true);
    }
    
    public RequestUI() {
        setSize(new Dimension(400, 300));
        setTitle("Requests");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initializeComponent();
    }
    
    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JLabel nameLabel = new JLabel("Name");
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(nameLabel, c);
        nameField = new JTextField();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(nameField, c);
        
        JLabel amountLabel = new JLabel("Amount");
        c = new GridBagConstraints();
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(amountLabel, c);
        amountField = new JTextField();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(amountField, c);
        
        JButton selectButton = new JButton("Request");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                select();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(selectButton, c);

        JLabel signalLabel = new JLabel("Signal");
        c = new GridBagConstraints();
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(signalLabel, c);
        signalField = new JTextField();
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(signalField, c);
        
        JButton signalButton = new JButton("Signal");
        signalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                signal();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 4;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(signalButton, c);

        JLabel addLabel = new JLabel("Process");
        c = new GridBagConstraints();
        c.gridy = 5;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(addLabel, c);
        processField = new JTextField("com.sample.contactCustomer");
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(processField, c);
        
        JButton addButton = new JButton("Dynamically add sub-process");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addSubProcessInstance();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 6;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(addButton, c);

        JButton addButton2 = new JButton("Dynamically add ad-hoc rules");
        addButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addRules();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 7;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(addButton2, c);
        
        ksession = createKnowledgeSession();
    }
    
    private void select() {
    	int id = ++this.requestId;
		Request request = new Request(id + "");
		request.setPersonId(nameField.getText());
		request.setAmount(Long.parseLong(amountField.getText()));
		if (ksession == null) {
			ksession = createKnowledgeSession();
		}
		ksession.insert(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("request", request.getId());
		processInstance = (WorkflowProcessInstance) ksession.startProcess("com.sample.requestHandling", params);
		ksession.insert(processInstance);
		// rule validation
		ksession.fireAllRules();
	}
    
    private void signal() {
    	ksession.signalEvent(signalField.getText(), null, processInstance.getId());
    }
    
    private void addSubProcessInstance() {
    	DynamicNodeInstance dynamicNodeInstance = (DynamicNodeInstance)
		processInstance.getNodeInstances().iterator().next();
    	DynamicUtils.addDynamicSubProcess(dynamicNodeInstance, ksession, "com.sample.contactCustomer", null);
    }
    
    private void addRules() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("request/adhoc.drl"), ResourceType.DRL);
		ksession.getKnowledgeBase().addKnowledgePackages(kbuilder.getKnowledgePackages());
		ksession.fireAllRules();
    }
    
    private StatefulKnowledgeSession createKnowledgeSession() {
    	try {
    		KnowledgeBase kbase = readKnowledgeBase();
    		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    		KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "test", 1000);
    		UIWorkItemHandler handler = new UIWorkItemHandler();
    		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
    		handler.setVisible(true);
    		ksession.getWorkItemManager().registerWorkItemHandler("Email", new WorkItemHandler() {
    			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    				System.out.println("Sending email ...");
    				manager.completeWorkItem(workItem.getId(), null);
    			}
    			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    			}
    		});
			Person person = new Person("john", "John Doe");
			ksession.insert(person);
			person = new Person("krisv", "Kris Verlaenen");
			ksession.insert(person);
			ksession.addEventListener(new DefaultProcessEventListener() {
				public void beforeProcessStarted(ProcessStartedEvent event) {
					ksession.insert(event);
				}
			});
			return ksession;
    	} catch (Throwable t) {
    		throw new RuntimeException("Could not initialize session!", t);
    	}
    }
    
	private KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("request/requestHandling.bpmn"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("request/contactCustomer.bpmn"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("request/validation.drl"), ResourceType.DRL);
		kbuilder.add(ResourceFactory.newClassPathResource("request/eventProcessing.drl"), ResourceType.DRL);
		kbuilder.add(ResourceFactory.newClassPathResource("request/exceptions.drl"), ResourceType.DRL);
		return kbuilder.newKnowledgeBase();
	}
	
}
